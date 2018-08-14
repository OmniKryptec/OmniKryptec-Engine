package omnikryptec.opencl.core;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.system.MemoryUtil;

public class CLContext {

	private static List<CLContext> contexts = new ArrayList<>();
	
	private CLContextCallback contextCB;
	private long context;
	
	public CLContext(CLDevice device) {
		context = CL10.clCreateContext(device.getParent().getCTXProps(), device.getID(), contextCB = CLContextCallback.create((errinfo, private_info, cb, user_data) -> {
            System.err.println("[LWJGL] cl_context_callback");
            System.err.println("\tInfo: " + MemoryUtil.memUTF8(errinfo));
        }), MemoryUtil.NULL, null);
	}
	
	public long getID() {
		return context;
	}
	
	CLContextCallback getCB() {
		return contextCB;
	}
	
	public static void cleanup() {
		for(CLContext c : contexts) {
			CL10.clReleaseContext(c.getID());
		}
	}
}
