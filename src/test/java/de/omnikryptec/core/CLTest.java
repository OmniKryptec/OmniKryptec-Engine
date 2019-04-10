package de.omnikryptec.core;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL10;

import de.omnikryptec.libapi.opencl.CLCommandQueue;
import de.omnikryptec.libapi.opencl.CLContext;
import de.omnikryptec.libapi.opencl.CLDevice;
import de.omnikryptec.libapi.opencl.CLKernel;
import de.omnikryptec.libapi.opencl.CLPlatform;
import de.omnikryptec.libapi.opencl.CLProgram;
import de.omnikryptec.libapi.opencl.OpenCL;

public class CLTest {
    
    private static final String KERNEL = "kernel void sum(global const float* a, global const float* b, global float* result, int const size) {\r\n"
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
        int size=100;
        FloatBuffer aBuff = BufferUtils.createFloatBuffer(size);
        System.out.println("Buffer a created");
        float[] tempData = new float[size];
        for(int i = 0; i < size; i++) {
            tempData[i] = i;
        }
        aBuff.put(tempData);
        aBuff.rewind();
        System.out.println("Finished Buffer a");
        // Create float array from size-1 to 0. This means that the result should be size-1 for each element.
        FloatBuffer bBuff = BufferUtils.createFloatBuffer(size);
        System.out.println("Buffer b created");
        for(int j = 0, i = size-1; j < size; j++, i--) {
            tempData[j] = i;
        }
        bBuff.put(tempData);
        bBuff.rewind();
        System.out.println("Finished Buffer b");
        FloatBuffer result = BufferUtils.createFloatBuffer(size);
        System.out.println("Buffer result created");
        kernel.setArg(0, aBuff);
        kernel.setArg(1, bBuff);
        kernel.setArg(2, result);
        kernel.setArg(3, size);
        System.out.println("Args set");
        kernel.enqueue(queue, 1, size, 0);
        queue.finish();
        System.out.println("Finished queue");
        System.out.println(result);
    }
}
