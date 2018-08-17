package omnikryptec.opencl.core;

import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL12;
import org.lwjgl.opencl.CL20;
import org.lwjgl.opencl.CL21;
import org.lwjgl.opencl.CL22;
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
	
	private static void createPlatformData() {
		CL10.clGetPlatformIDs(null, tmpBuffer);
		assert tmpBuffer.get(0)!=0;
		platforms = memStack.mallocPointer(tmpBuffer.get(0));
		CL10.clGetPlatformIDs(platforms, (IntBuffer)null);
	}
	
	public static PointerBuffer getPlatforms() {
		return platforms;
	}
	
	public static CLPlatform getPlatform(int platformInd) {
		if(!createdPlatforms.containsKey(platformInd)) {
			createdPlatforms.put(platformInd, new CLPlatform(platforms.get(platformInd)));
		}
		return createdPlatforms.get(platformInd);

	}
	
	public static void cleanup() {
		CLKernel.cleanup();
		CLProgram.cleanup();
		CLMemory.cleanup();
		CLCommandQueue.cleanup();
		CLContext.cleanup();
		CL.destroy();
	}
	
	public static void create() {
		try {
			CL.create();
		}catch(Exception e) {}
		createPlatformData();
	}
	
	private static final Class<?>[] constantsClasses = {CL10.class, CL12.class, CL20.class, CL21.class, CL22.class};
	public static String searchConstants(int i) {
		for(Class<?> c : constantsClasses) {
			Field[] fields = c.getFields();
			for(Field f : fields) {
				try {
					if(i==f.getInt(null)) {
						return f.getName();
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		return "ERROR 404: Constant not found";
	}
}
