package omnikryptec.opencl.core;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opencl.CL10;

public class CLMemory {

	private static List<CLMemory> memorys = new ArrayList<>();
	
	private long id;
	
	public CLMemory(CLContext context, int memOptions, FloatBuffer buffer) {
		id = CL10.clCreateBuffer(context.getID(), memOptions, buffer, null);
	}
	
	public long getID() {
		return id;
	}
	
	public static void cleanup() {
		for(CLMemory m : memorys) {
			CL10.clReleaseMemObject(m.getID());
		}
	}
}
