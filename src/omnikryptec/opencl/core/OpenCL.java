package omnikryptec.opencl.core;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.system.MemoryStack;

public class OpenCL {

	private static HashMap<Integer, CLPlatform> createdPlatforms = new HashMap<>();
	
	static MemoryStack memStack;
	static IntBuffer tmpBuffer;
	private static PointerBuffer platforms;
	
	static {
		memStack = MemoryStack.stackPush();
		tmpBuffer = memStack.mallocInt(1);
	}
	
	public OpenCL createPlatformData() {
		CL10.clGetPlatformIDs(null, tmpBuffer);
		assert tmpBuffer.get(0)!=0;
		platforms = memStack.mallocPointer(tmpBuffer.get(0));
		CL10.clGetPlatformIDs(platforms, (IntBuffer)null);
		return this;
	}
	
	public PointerBuffer getPlatforms() {
		return platforms;
	}
	
	public CLPlatform getPlatform(int platformInd) {
		if(createdPlatforms.containsKey(platformInd)) {
			return createdPlatforms.get(platformInd);
		}else {
			return createdPlatforms.put(platformInd, new CLPlatform(platforms.get(platformInd)));
		}
	}
	
	public static void cleanup() {
		CLKernel.cleanup();
		CLProgram.cleanup();
		
		CLMemory.cleanup();
		
		CLContext.cleanup();
		CL.destroy();
	}
	
	
}
