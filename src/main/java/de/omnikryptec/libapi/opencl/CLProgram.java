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

package de.omnikryptec.libapi.opencl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL10;

public class CLProgram {

    private static List<CLProgram> programs = new ArrayList<>();

    private final long id;

    public CLProgram(final CLContext context, final CharSequence source) {
        this.id = CL10.clCreateProgramWithSource(context.getID(), source, OpenCL.tmpBuffer);
        OpenCL.checked(OpenCL.tmpBuffer.get(0));
    }

    public static void cleanup() {
        for (final CLProgram p : programs) {
            CL10.clReleaseProgram(p.getID());
        }
    }

    public long getID() {
        return this.id;
    }

    public CLProgram build(final CLDevice device, final int errorsize, final CharSequence options) {
        final int error = CL10.clBuildProgram(this.id, device.getID(), options, null, 0);
        if (error != CL10.CL_SUCCESS) {
            System.err.println("==== Build error ====");
            final ByteBuffer buffer = BufferUtils.createByteBuffer(errorsize);
            CL10.clGetProgramBuildInfo(getID(), device.getID(), CL10.CL_PROGRAM_BUILD_LOG, buffer, null);
            final byte[] array = new byte[errorsize];
            for (int i = 0; i < array.length; i++) {
                array[i] = buffer.get(i);
            }
            System.err.println(new String(array));
            OpenCL.checked(error);
        }
        return this;
    }
}
