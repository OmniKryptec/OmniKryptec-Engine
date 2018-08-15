package omnikryptec.opencl.core;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opencl.CL10;

public class CLKernel {
	
	private static List<CLKernel> kernels = new ArrayList<>();
	
	private long id;
	
	public CLKernel(CLProgram prog, String method) {
		id = CL10.clCreateKernel(prog.getID(), method, OpenCL.tmpBuffer);
		if(OpenCL.tmpBuffer.get(0)!=CL10.CL_SUCCESS) {
			System.err.println("OpenCL Kernel Err: "+OpenCL.searchConstants(OpenCL.tmpBuffer.get(0)));
		}
	}
	
	public long getID() {
		return id;
	}
	
	public CLKernel setArg(int i, FloatBuffer buffer) {
		CL10.clSetKernelArg(getID(), i, buffer);
		return this;
	}
	
	public void enqueue(CLCommandQueue queue, int dim, int worksize_gl, int worksize_loc) {
		//CL10.clEnqueueNDRangeKernel(queue.getID(), getID(), 0, null, null, null, null, null);
		CL10.nclEnqueueNDRangeKernel(queue.getID(), getID(), dim, 0, worksize_gl, worksize_loc, 0, 0, 0);
	}
	
	public static void cleanup() {
		for(CLKernel k : kernels) {
			CL10.clReleaseKernel(k.getID());
		}
	}

	public CLKernel setArg(int i, int someInt) {
		CL10.clSetKernelArg(getID(), i, someInt);
		return this;
	}

}
