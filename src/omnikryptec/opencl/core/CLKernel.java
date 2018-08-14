package omnikryptec.opencl.core;

import java.util.ArrayList;
import java.util.List;

public class CLKernel {
	
	private static List<CLKernel> kernels = new ArrayList<>();
	
	public static void cleanup() {
		for(CLKernel k : kernels) {
			
		}
	}

}
