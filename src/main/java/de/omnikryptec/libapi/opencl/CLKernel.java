/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.libapi.opencl;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;

public class CLKernel {
    
    private static List<CLKernel> kernels = new ArrayList<>();
    
    private final long id;
    
    public CLKernel(final CLProgram prog, final String method) {
        this.id = CL10.clCreateKernel(prog.getID(), method, OpenCL.tmpBuffer);
        OpenCL.checked(OpenCL.tmpBuffer.get(0));
    }
    
    public static void cleanup() {
        for (final CLKernel k : kernels) {
            CL10.clReleaseKernel(k.getID());
        }
    }
    
    public long getID() {
        return this.id;
    }
    
    public void enqueue(final CLCommandQueue queue, final int dim, final int worksize_gl, final int worksize_loc) {
        PointerBuffer global = BufferUtils.createPointerBuffer(1);
        global.put(0, worksize_gl);
        int i = CL10.clEnqueueNDRangeKernel(queue.getID(), getID(), dim, null, global, /* TODO worksize local */null,
                null, null);
        OpenCL.checked(i);
    }
    
    public CLKernel setArg(int i, CLMemory mem) {
        int k = CL10.clSetKernelArg1p(getID(), i, mem.getID());
        OpenCL.checked(k);
        return this;
    }
    
    public CLKernel setArg(final int i, final int someInt) {
        int k = CL10.clSetKernelArg1i(getID(), i, someInt);
        OpenCL.checked(k);
        return this;
    }
    
    public CLKernel setArg(final int i, final float someFloat) {
        int k = CL10.clSetKernelArg1f(getID(), i, someFloat);
        OpenCL.checked(k);
        return this;
    }
    
}
