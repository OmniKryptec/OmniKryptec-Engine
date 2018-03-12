package omnikryptec.opencl.core;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL;

public class OpenCL {
	public void test() {
		CL.destroy();
	}
	
	
	
	public static void cleanup() {
		CL.destroy();
	}
}
