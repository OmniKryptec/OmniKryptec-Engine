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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opencl.CL10;

public class CLMemory {
    
    private static List<CLMemory> memorys = new ArrayList<>();
    
    public static void cleanup() {
        for (final CLMemory m : memorys) {
            CL10.clReleaseMemObject(m.getID());
        }
    }
    
    private final long id;
    
    public CLMemory(final CLContext context, final int memOpt, final int size) {
        this.id = CL10.clCreateBuffer(context.getID(), memOpt, size, OpenCL.tmpBuffer);
        OpenCL.checked(OpenCL.tmpBuffer.get(0));
    }
    
    public CLMemory(final CLContext context, final int memOptions, final FloatBuffer buffer) {
        this.id = CL10.clCreateBuffer(context.getID(), memOptions, buffer, OpenCL.tmpBuffer);
        OpenCL.checked(OpenCL.tmpBuffer.get(0));
    }
    
    public void enqueueWriteBuffer(final CLCommandQueue queue, final FloatBuffer floats) {
        final int i = CL10.clEnqueueWriteBuffer(queue.getID(), getID(), false, 0, floats, null, null);
        OpenCL.checked(i);
    }
    
    public void enqueueReadBuffer(final CLCommandQueue queue, final FloatBuffer buffer) {
        final int i = CL10.clEnqueueReadBuffer(queue.getID(), getID(), false, 0, buffer, null, null);
        OpenCL.checked(i);
    }
    
    public long getID() {
        return this.id;
    }
}
