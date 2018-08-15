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
	private int devs = -1;
	
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
		devs = OpenCL.tmpBuffer.get(0);
		devices = OpenCL.memStack.mallocPointer(devs);
		CL10.clGetDeviceIDs(platform, clDeviceFilter, devices, (IntBuffer)null);
		return this;
	}
	
	public PointerBuffer getDevices() {
		return devices;
	}
	
	public int getDevicesSize() {
		return devs;
	}
	
	public CLDevice getDevice(int deviceInd) {
		if(!createdDevices.containsKey(deviceInd)) {
			createdDevices.put(deviceInd, new CLDevice(devices.get(deviceInd), this));
		}
		return createdDevices.get(deviceInd);

	}

	
}
