package omnikryptec.opencl.core;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCapabilities;

public class CLPlatform {

	private static HashMap<Integer, CLDevice> createdDevices = new HashMap<>();
	
	private CLCapabilities platformCaps;
	private PointerBuffer devices,ctxProps;
	private long platform;
	
	CLPlatform(long id) {
		this.platform = id;
		platformCaps  = CL.createPlatformCapabilities(id);
		ctxProps = OpenCL.memStack.mallocPointer(3);
		ctxProps.put(0, CL10.CL_CONTEXT_PLATFORM).put(2, 0);
		ctxProps.put(1, id);
	}

	public CLCapabilities getPlatCaps() {
		return platformCaps;
	}

	public long getID() {
		return platform;
	}
	
	public PointerBuffer getCTXProps() {
		return ctxProps;
	}
	
	public CLPlatform createDeviceData(int clDeviceFilter) {
		CL10.clGetDeviceIDs(platform, clDeviceFilter, null, OpenCL.tmpBuffer);
		devices = OpenCL.memStack.mallocPointer(OpenCL.tmpBuffer.get(0));
		CL10.clGetDeviceIDs(platform, clDeviceFilter, devices, (IntBuffer)null);
		return this;
	}
	
	public CLDevice getDevice(int deviceInd) {
		if(createdDevices.containsKey(deviceInd)) {
			return createdDevices.get(deviceInd);
		}else {
			return createdDevices.put(deviceInd, new CLDevice(devices.get(deviceInd), this));
		}
	}
	
}
