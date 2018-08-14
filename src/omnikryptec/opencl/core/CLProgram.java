package omnikryptec.opencl.core;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLProgramCallbackI;

public class CLProgram {
	
	private static List<CLProgram> programs = new ArrayList<>();
	
	private long id;
	private CLProgramCallbackI clpi;
	
	public CLProgram(CLContext context, CharSequence source) {
		id = CL10.clCreateProgramWithSource(context.getID(), source, OpenCL.tmpBuffer);
		if(OpenCL.tmpBuffer.get(0)!=CL10.CL_SUCCESS) {
			System.err.println("OpenCL Prog Err: "+OpenCL.tmpBuffer.get(0));
		}
	}
	
	public long getID() {
		return id;
	}
	
	CLProgramCallbackI getProgCB() {
		return clpi;
	}
	
	public CLProgram build(CLDevice device) {
		int error = CL10.clBuildProgram(id, device.getID(), null, clpi = new CLProgramCallbackI() {
			
			@Override
			public void invoke(long arg0, long arg1) {				
			}
		}, 0);
		if(error!=CL10.CL_SUCCESS) {
			System.err.println("OpenCL Build Err: "+error);
		}
		return this;
	}
	
	public static void cleanup() {
		for(CLProgram p : programs) {
			CL10.clReleaseProgram(p.getID());
		}
	}
}
