package de.omnikryptec.core;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL10;

import de.omnikryptec.libapi.opencl.CLCommandQueue;
import de.omnikryptec.libapi.opencl.CLContext;
import de.omnikryptec.libapi.opencl.CLDevice;
import de.omnikryptec.libapi.opencl.CLKernel;
import de.omnikryptec.libapi.opencl.CLMemory;
import de.omnikryptec.libapi.opencl.CLPlatform;
import de.omnikryptec.libapi.opencl.CLProgram;
import de.omnikryptec.libapi.opencl.OpenCL;

public class CLTest {
    
    private static final String KERNEL = "__kernel void sum(__global const float *a, __global const float *b, __global float *result, int const size) {\r\n"
            + "    const int itemId = get_global_id(0); \r\n" + "    if(itemId < size) {\r\n"
            + "        result[itemId] = a[itemId] + b[itemId];\r\n" + "    }\r\n" + "} ";
    
    public static void main(String[] args) {
        OpenCL.create();
        System.out.println("Created OpenCL context");
        CLPlatform platform = OpenCL.getPlatform(0);
        CLDevice device = platform.createDeviceData(CL10.CL_DEVICE_TYPE_ALL).getDevice(0);
        CLContext context = new CLContext(device);
        CLCommandQueue queue = new CLCommandQueue(context, device, CL10.CL_QUEUE_PROFILING_ENABLE);
        CLProgram program = new CLProgram(context, KERNEL).build(device, 1024);
        CLKernel kernel = new CLKernel(program, "sum");
        System.out.println("Created objects");
        int size = 100;
        float[] aArray = new float[size];
        for (int i = 0; i < size; i++) {
            aArray[i] = i;
        }
        FloatBuffer aBuff = BufferUtils.createFloatBuffer(size);
        aBuff.put(aArray);
        aBuff.rewind();
        CLMemory aMem = new CLMemory(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, aBuff);
        
        // Create float array from size-1 to 0. This means that the result should be size-1 for each element.
        float[] bArray = new float[size];
        for (int j = 0, i = size - 1; j < size; j++, i--) {
            bArray[j] = i;
        }
        FloatBuffer bBuff = BufferUtils.createFloatBuffer(size);
        bBuff.put(bArray);
        bBuff.rewind();
        CLMemory bMem = new CLMemory(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, bBuff);
        aMem.enqueueWriteBuffer(queue, aBuff);
        bMem.enqueueWriteBuffer(queue, bBuff);
        queue.finish();
        FloatBuffer result = BufferUtils.createFloatBuffer(size);
        CLMemory resultMem = new CLMemory(context, CL10.CL_MEM_WRITE_ONLY, 4 * size);
        System.out.println("Buffer result created");
        kernel.setArg(3, size);
        // CL10.nclSetKernelArg(kernel.getID(), 0,4, aMem.getID());
        // CL10.nclSetKernelArg(kernel.getID(), 1,4, bMem.getID());
        // CL10.nclSetKernelArg(kernel.getID(), 2,4, resultMem.getID());
        kernel.setArgp(0, aMem.getID());
        kernel.setArgp(1, bMem.getID());
        kernel.setArgp(2, resultMem.getID());
        
        //kernel.setArg(0, aBuff);
        // kernel.setArg(1, bBuff);
        //kernel.setArg(2, result);
        System.out.println("Args set");
        kernel.enqueue(queue, 1, size, 0);
        System.out.println("Enqueued");
        resultMem.enqueueReadBuffer(queue, result);
        queue.finish();
        System.out.println("Finished queue");
        for (int i = 0; i < size; i++) {
            System.out.print(result.get(i) + " ");
        }
        System.out.println();
    }
}
