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

import org.lwjgl.opencl.CL10;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class CLKernel {
	
	private static List<CLKernel> kernels = new ArrayList<>();
	
	private long id;
	
	public CLKernel(CLProgram prog, String method) {
		id = CL10.clCreateKernel(prog.getID(), method, OpenCL.tmpBuffer);
		if(OpenCL.tmpBuffer.get(0)!=CL10.CL_SUCCESS) {
			System.err.println("OpenCL Kernel Err: "+OpenCL.searchConstants(OpenCL.tmpBuffer.get(0)));
		}
	}
	
	public long getID() {
		return id;
	}
	
	public CLKernel setArg(int i, FloatBuffer buffer) {
		CL10.clSetKernelArg(getID(), i, buffer);
		return this;
	}
	
	public void enqueue(CLCommandQueue queue, int dim, int worksize_gl, int worksize_loc) {
		//CL10.clEnqueueNDRangeKernel(queue.getID(), getID(), 0, null, null, null, null, null);
		CL10.nclEnqueueNDRangeKernel(queue.getID(), getID(), dim, 0, worksize_gl, worksize_loc, 0, 0, 0);
	}
	
	public static void cleanup() {
		for(CLKernel k : kernels) {
			CL10.clReleaseKernel(k.getID());
		}
	}

	public CLKernel setArg(int i, int someInt) {
		CL10.clSetKernelArg(getID(), i, someInt);
		return this;
	}

}
