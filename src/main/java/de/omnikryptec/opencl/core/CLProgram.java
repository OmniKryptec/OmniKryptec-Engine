/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.opencl.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL10;

public class CLProgram {
	
	private static List<CLProgram> programs = new ArrayList<>();
	
	private long id;
	
	public CLProgram(CLContext context, CharSequence source) {
		id = CL10.clCreateProgramWithSource(context.getID(), source, OpenCL.tmpBuffer);
		if(OpenCL.tmpBuffer.get(0)!=CL10.CL_SUCCESS) {
			System.err.println("OpenCL Prog Err: "+OpenCL.searchConstants(OpenCL.tmpBuffer.get(0)));
		}
	}
	
	public long getID() {
		return id;
	}
	
	
	public CLProgram build(CLDevice device, int errorsize) {
		int error = CL10.clBuildProgram(id, device.getID(), "", null, 0);
		if(error!=CL10.CL_SUCCESS) {
			System.err.println("OpenCL Build Err: "+OpenCL.searchConstants(error));
			ByteBuffer buffer = BufferUtils.createByteBuffer(errorsize);
			CL10.clGetProgramBuildInfo(getID(), device.getID(), CL10.CL_PROGRAM_BUILD_LOG, buffer, null);
			byte[] array = new byte[errorsize];
			for(int i=0; i<array.length; i++) {
				array[i] = buffer.get(i);
			}
			System.err.println(new String(array));
		}
		return this;
	}
	
	public static void cleanup() {
		for(CLProgram p : programs) {
			CL10.clReleaseProgram(p.getID());
		}
	}
}
