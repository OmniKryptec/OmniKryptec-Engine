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

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opencl.CL10;

public class CLCommandQueue {
	
	private static List<CLCommandQueue> queues = new ArrayList<>();
	
	private long id;
	
	public CLCommandQueue(CLContext context, CLDevice device, int options) {
		id = CL10.clCreateCommandQueue(context.getID(), device.getID(), options, OpenCL.tmpBuffer);
		if(OpenCL.tmpBuffer.get(0)!=CL10.CL_SUCCESS) {
			System.err.println("OpenCL ComQueue Err: "+OpenCL.tmpBuffer.get(0));
		}
	}
	
	public long getID() {
		return id;
	}
	
	public void finish() {
		CL10.clFinish(getID());
	}
	
	public static void cleanup() {
		for(CLCommandQueue q : queues) {
			CL10.clReleaseCommandQueue(q.getID());
		}
	}
}
