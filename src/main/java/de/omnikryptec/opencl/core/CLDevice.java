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

import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCapabilities;

public class CLDevice {

	private CLCapabilities deviceCaps;
	private CLPlatform parent;
	private long device;

	CLDevice(long id, CLPlatform parent) {
		this.device = id;
		this.parent = parent;
		deviceCaps = CL.createDeviceCapabilities(id, parent.getPlatCaps());
	}

	public CLCapabilities getDeviceCaps() {
		return deviceCaps;
	}

	public long getID() {
		return device;
	}

	public CLPlatform getParent() {
		return parent;
	}

//	public DeviceType[] getTypes() {
//		PointerBuffer buffer = BufferUtils.createPointerBuffer(1);
//		//buffer.put(1);
//		CL10.clGetDeviceInfo(device, CL10.CL_DEVICE_TYPE, OpenCL.tmpBuffer, buffer);
//		int i = OpenCL.tmpBuffer.get(0);
//		return DeviceType.toTypes(i);
//	}
}
