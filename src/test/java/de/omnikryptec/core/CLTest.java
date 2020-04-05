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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL10;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
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

    public static void main(final String[] args) {
        //Create stuff
        final Settings<LibSetting> s = new Settings<>();
        s.set(LibSetting.DEBUG, true);
        s.set(LibSetting.DEBUG_LIBRARY_LOADING, true);
        LibAPIManager.init(s);
        LibAPIManager.instance().initOpenCL();
        final OpenCL opc = LibAPIManager.instance().getOpenCL();
        final CLPlatform platform = opc.getPlatform(0);
        final CLDevice device = platform.createDeviceData(DeviceType.GPU).getDevice(0);
        final CLContext context = new CLContext(device);
        final CLCommandQueue queue = new CLCommandQueue(context, device, CL10.CL_QUEUE_PROFILING_ENABLE);
        final CLProgram program = new CLProgram(context, KERNEL).build(device, 1024, "");
        final CLKernel kernel = new CLKernel(program, "sum");
        final FloatBuffer aBuff = BufferUtils.createFloatBuffer(size);
        final FloatBuffer bBuff = BufferUtils.createFloatBuffer(size);
        final CLMemory aMem = new CLMemory(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, aBuff);
        final CLMemory bMem = new CLMemory(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, bBuff);
        final CLMemory resultMem = new CLMemory(context, CL10.CL_MEM_WRITE_ONLY, 4 * size);
        //Fill read buffers
        final float[] aArray = new float[size];
        for (int i = 0; i < size; i++) {
            aArray[i] = i;
        }
        aBuff.put(aArray);
        aBuff.rewind();
        final float[] bArray = new float[size];
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
        final FloatBuffer result = BufferUtils.createFloatBuffer(size);
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
