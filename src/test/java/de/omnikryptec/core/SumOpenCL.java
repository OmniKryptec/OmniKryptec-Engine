/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.core;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public final class SumOpenCL {
    
    private static final String sumProgramSource = "kernel void sum(global const float* a, global const float* b, global float* result, int const size) {"
            + "  const int itemId = get_global_id(0);" + "  if(itemId < size) {"
            + "    result[itemId] = a[itemId] + b[itemId];" + "  }" + "}";
    
    private CLContextCallback clContextCB;
    private long clContext;
    private IntBuffer errcode_ret;
    private long clKernel;
    private long clDevice;
    private long clQueue;
    private long sumProgram;
    private long aMemory;
    private long bMemory;
    private long clPlatform;
    private CLCapabilities clPlatformCapabilities;
    private long resultMemory;
    private static final int size = 100;
    
    private static void checkCLError(final IntBuffer i) {
        
    }
    
    private static void checkCLError(final int i) {
        
    }
    
    public void run() {
        initializeCL();
        
        this.sumProgram = CL10.clCreateProgramWithSource(this.clContext, sumProgramSource, this.errcode_ret);
        
        int errcode = clBuildProgram(this.sumProgram, this.clDevice, "", null, NULL);
        checkCLError(errcode);
        
        // init kernel with constants
        this.clKernel = clCreateKernel(this.sumProgram, "sum", this.errcode_ret);
        checkCLError(this.errcode_ret);
        
        createMemory();
        
        clSetKernelArg1p(this.clKernel, 0, this.aMemory);
        clSetKernelArg1p(this.clKernel, 1, this.bMemory);
        clSetKernelArg1p(this.clKernel, 2, this.resultMemory);
        clSetKernelArg1i(this.clKernel, 3, size);
        
        final int dimensions = 1;
        final PointerBuffer globalWorkSize = BufferUtils.createPointerBuffer(dimensions); // In here we put
        // the total number
        // of work items we
        // want in each
        // dimension.
        globalWorkSize.put(0, size); // Size is a variable we defined a while back showing how many
                                     // elements are in our arrays.
        
        // Run the specified number of work units using our OpenCL program kernel
        errcode = clEnqueueNDRangeKernel(this.clQueue, this.clKernel, dimensions, null, globalWorkSize, null, null,
                null);
        
        CL10.clFinish(this.clQueue);
        
        printResults();
        
        cleanup();
    }
    
    private void printResults() {
        // This reads the result memory buffer
        final FloatBuffer resultBuff = BufferUtils.createFloatBuffer(size);
        // We read the buffer in blocking mode so that when the method returns we know that the result
        // buffer is full
        CL10.clEnqueueReadBuffer(this.clQueue, this.resultMemory, true, 0, resultBuff, null, null);
        // Print the values in the result buffer
        for (int i = 0; i < resultBuff.capacity(); i++) {
            System.out.println("result at " + i + " = " + resultBuff.get(i));
        }
        // This should print out 100 lines of result floats, each being 99.
    }
    
    private void createMemory() {
        // Create OpenCL memory object containing the first buffer's list of numbers
        this.aMemory = CL10.clCreateBuffer(this.clContext, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR,
                getABuffer(), this.errcode_ret);
        checkCLError(this.errcode_ret);
        
        // Create OpenCL memory object containing the second buffer's list of numbers
        this.bMemory = CL10.clCreateBuffer(this.clContext, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR,
                getBBuffer(), this.errcode_ret);
        checkCLError(this.errcode_ret);
        
        // Remember the length argument here is in bytes. 4 bytes per float.
        this.resultMemory = CL10.clCreateBuffer(this.clContext, CL10.CL_MEM_READ_ONLY, size * 4, this.errcode_ret);
        checkCLError(this.errcode_ret);
    }
    
    private FloatBuffer getABuffer() {
        // Create float array from 0 to size-1.
        final FloatBuffer aBuff = BufferUtils.createFloatBuffer(size);
        final float[] tempData = new float[size];
        for (int i = 0; i < size; i++) {
            tempData[i] = i;
            System.out.println("a[" + i + "]=" + i);
        }
        aBuff.put(tempData);
        aBuff.rewind();
        return aBuff;
    }
    
    private FloatBuffer getBBuffer() {
        // Create float array from size-1 to 0. This means that the result should be size-1 for each
        // element.
        final FloatBuffer bBuff = BufferUtils.createFloatBuffer(size);
        final float[] tempData = new float[size];
        for (int j = 0, i = size - 1; j < size; j++, i--) {
            tempData[j] = i;
            System.out.println("b[" + j + "]=" + i);
        }
        bBuff.put(tempData);
        bBuff.rewind();
        return bBuff;
    }
    
    private void cleanup() {
        // Destroy our kernel and program
        CL10.clReleaseCommandQueue(this.clQueue);
        CL10.clReleaseKernel(this.clKernel);
        CL10.clReleaseProgram(this.sumProgram);
        
        // Destroy our memory objects
        CL10.clReleaseMemObject(this.aMemory);
        CL10.clReleaseMemObject(this.bMemory);
        CL10.clReleaseMemObject(this.resultMemory);
        
        // Not strictly necessary
        CL.destroy();
    }
    
    public void initializeCL() {
        this.errcode_ret = BufferUtils.createIntBuffer(1);
        
        // Get the first available platform
        try (MemoryStack stack = stackPush()) {
            final IntBuffer pi = stack.mallocInt(1);
            checkCLError(clGetPlatformIDs(null, pi));
            if (pi.get(0) == 0) {
                throw new IllegalStateException("No OpenCL platforms found.");
            }
            
            final PointerBuffer platformIDs = stack.mallocPointer(pi.get(0));
            checkCLError(clGetPlatformIDs(platformIDs, (IntBuffer) null));
            
            for (int i = 0; i < platformIDs.capacity() && i == 0; i++) {
                final long platform = platformIDs.get(i);
                this.clPlatformCapabilities = CL.createPlatformCapabilities(platform);
                this.clPlatform = platform;
            }
        }
        
        this.clDevice = getDevice(this.clPlatform, this.clPlatformCapabilities, CL_DEVICE_TYPE_GPU);
        
        // Create the context
        final PointerBuffer ctxProps = BufferUtils.createPointerBuffer(7);
        ctxProps.put(CL_CONTEXT_PLATFORM).put(this.clPlatform).put(NULL).flip();
        
        this.clContext = clCreateContext(
                ctxProps, this.clDevice, this.clContextCB = CLContextCallback.create((errinfo, private_info, cb,
                        user_data) -> System.out.printf("cl_context_callback\n\tInfo: %s", memUTF8(errinfo))),
                NULL, this.errcode_ret);
        
        // create command queue
        this.clQueue = clCreateCommandQueue(this.clContext, this.clDevice, NULL, this.errcode_ret);
        checkCLError(this.errcode_ret);
    }
    
    private static long getDevice(final long platform, final CLCapabilities platformCaps, final int deviceType) {
        try (MemoryStack stack = stackPush()) {
            final IntBuffer pi = stack.mallocInt(1);
            checkCLError(clGetDeviceIDs(platform, deviceType, null, pi));
            
            final PointerBuffer devices = stack.mallocPointer(pi.get(0));
            checkCLError(clGetDeviceIDs(platform, deviceType, devices, (IntBuffer) null));
            
            for (int i = 0; i < devices.capacity(); i++) {
                final long device = devices.get(i);
                
                final CLCapabilities caps = CL.createDeviceCapabilities(device, platformCaps);
                if (!(caps.cl_khr_gl_sharing || caps.cl_APPLE_gl_sharing)) {
                    continue;
                }
                
                return device;
            }
        }
        
        return NULL;
    }
    
    public static void main(final String... args) {
        final SumOpenCL clApp = new SumOpenCL();
        clApp.run();
    }
    
}