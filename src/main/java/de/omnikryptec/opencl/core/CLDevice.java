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
