package omnikryptec.opencl.core;

import java.util.HashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCapabilities;

public class CLDevice {

	private CLCapabilities deviceCaps;
	private CLPlatform parent;
	private long device;
	
	CLDevice(long id, CLPlatform parent){
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
	
	//public 
}
