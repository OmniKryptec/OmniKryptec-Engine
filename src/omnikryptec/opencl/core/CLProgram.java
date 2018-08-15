package omnikryptec.opencl.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLProgramCallbackI;

public class CLProgram {
	
	private static List<CLProgram> programs = new ArrayList<>();
	
	private long id;
	
	public CLProgram(CLContext context, CharSequence source) {
		id = CL10.clCreateProgramWithSource(context.getID(), source, OpenCL.tmpBuffer);
		if(OpenCL.tmpBuffer.get(0)!=CL10.CL_SUCCESS) {
			System.err.println("OpenCL Prog Err: "+OpenCL.searchConstants(OpenCL.tmpBuffer.get(0)));
		}
	}
	
	public long getID() {
		return id;
	}
	
	
	public CLProgram build(CLDevice device, int errorsize) {
		int error = CL10.clBuildProgram(id, device.getID(), "", null, 0);
		if(error!=CL10.CL_SUCCESS) {
			System.err.println("OpenCL Build Err: "+OpenCL.searchConstants(error));
			ByteBuffer buffer = BufferUtils.createByteBuffer(errorsize);
			CL10.clGetProgramBuildInfo(getID(), device.getID(), CL10.CL_PROGRAM_BUILD_LOG, buffer, null);
			byte[] array = new byte[errorsize];
			for(int i=0; i<array.length; i++) {
				array[i] = buffer.get(i);
			}
			System.err.println(new String(array));
		}
		return this;
	}
	
	public static void cleanup() {
		for(CLProgram p : programs) {
			CL10.clReleaseProgram(p.getID());
		}
	}
}
