package de.omnikryptec.core;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL10;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.opencl.CLCommandQueue;
import de.omnikryptec.libapi.opencl.CLContext;
import de.omnikryptec.libapi.opencl.CLDevice;
import de.omnikryptec.libapi.opencl.CLKernel;
import de.omnikryptec.libapi.opencl.CLMemory;
import de.omnikryptec.libapi.opencl.CLPlatform;
import de.omnikryptec.libapi.opencl.CLProgram;
import de.omnikryptec.libapi.opencl.DeviceType;
import de.omnikryptec.libapi.opencl.OpenCL;
import de.omnikryptec.util.settings.Settings;

public class CLTest {
    
    private static final String KERNEL = "__kernel void sum(__global const float *a, __global const float *b, __global float *result, int const size) {\r\n"
            + "    const int itemId = get_global_id(0); \r\n" + "    if(itemId < size) {\r\n"
            + "        result[itemId] = a[itemId] + b[itemId];\r\n" + "    }\r\n" + "} ";
    
    private static final int size = 10;
    
    public static void main(String[] args) {
        //Create stuff
        LibAPIManager.init(new Settings<>());
        LibAPIManager.instance().initOpenCL();
        OpenCL opc = LibAPIManager.instance().getOpenCL();
        CLPlatform platform = opc.getPlatform(0);
        CLDevice device = platform.createDeviceData(DeviceType.GPU).getDevice(0);
        CLContext context = new CLContext(device);
        CLCommandQueue queue = new CLCommandQueue(context, device, CL10.CL_QUEUE_PROFILING_ENABLE);
        CLProgram program = new CLProgram(context, KERNEL).build(device, 1024, "");
        CLKernel kernel = new CLKernel(program, "sum");
        FloatBuffer aBuff = BufferUtils.createFloatBuffer(size);
        FloatBuffer bBuff = BufferUtils.createFloatBuffer(size);
        CLMemory aMem = new CLMemory(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, aBuff);
        CLMemory bMem = new CLMemory(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, bBuff);
        CLMemory resultMem = new CLMemory(context, CL10.CL_MEM_WRITE_ONLY, 4 * size);
        //Fill read buffers
        float[] aArray = new float[size];
        for (int i = 0; i < size; i++) {
            aArray[i] = i;
        }
        aBuff.put(aArray);
        aBuff.rewind();
        float[] bArray = new float[size];
        for (int j = 0, i = size - 1; j < size; j++, i--) {
            bArray[j] = i;
        }
        bBuff.put(bArray);
        bBuff.rewind();
        //write buffers
        aMem.enqueueWriteBuffer(queue, aBuff);
        bMem.enqueueWriteBuffer(queue, bBuff);
        queue.finish();
        //enqueue kernel
        kernel.setArg(3, size);
        kernel.setArg(0, aMem);
        kernel.setArg(1, bMem);
        kernel.setArg(2, resultMem);
        kernel.enqueue(queue, 1, size, 0);
        //cope with result
        FloatBuffer result = BufferUtils.createFloatBuffer(size);
        resultMem.enqueueReadBuffer(queue, result);
        queue.finish();
        for (int i = 0; i < size; i++) {
            System.out.print(result.get(i) + " ");
            if ((i + 1) % 100 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }
}
