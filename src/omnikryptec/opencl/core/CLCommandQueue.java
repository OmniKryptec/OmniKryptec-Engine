package omnikryptec.opencl.core;

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
