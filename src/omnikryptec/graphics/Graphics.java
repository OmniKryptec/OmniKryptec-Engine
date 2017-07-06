package omnikryptec.graphics;

import java.util.HashMap;
import java.util.Map;

public abstract class Graphics {
	
	private Map<String, Integer> constants = new HashMap<>();
	
	public Graphics(Map<String, Integer> constants){
		this.constants = constants;
	}
	
	//Rendering
	public abstract void drawElements();
	
	
	//Shader
	public abstract void loadMatrix4f();
	public abstract int getUniformLocation();
	public abstract int createProgram();
	public abstract void attachShader();
	public abstract void linkProgram();
	public abstract void validateProgram();
	public abstract void useProgram();
	public abstract void deleteShader();
	public abstract void detachShader();
	public abstract void deleteProgram();
}
