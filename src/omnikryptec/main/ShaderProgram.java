package omnikryptec.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public abstract class ShaderProgram {
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	private Map<String, Integer> uniforms_map = new HashMap<>();
	
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public ShaderProgram(String vertexFile, String fragmentFile) {
		ShaderHolder vertexShaderHolder = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		ShaderHolder fragmentShaderHolder = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		vertexShaderID = vertexShaderHolder.getID();
		fragmentShaderID = fragmentShaderHolder.getID();
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		String tmp;
		for(int i=0; i<vertexShaderHolder.getUniformLines().size(); i++){
			tmp = vertexShaderHolder.getUniformLines().get(i).split(" ")[2];
			if(uniforms_map.containsKey(tmp)){
				
			}else{
				uniforms_map.put(tmp, getUniformLocation(tmp));
			}
		}
		getAllUniformLocations();
	}

	@Deprecated
	protected abstract void getAllUniformLocations();

	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}

	public void start() {
		GL20.glUseProgram(programID);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	public void cleanup() {
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}

	protected abstract void bindAttributes();

	protected void bindAttribute(int attrib, String varName) {
		GL20.glBindAttribLocation(programID, attrib, varName);
	}

	protected void loadUpFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	protected void loadUpInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	protected void loadUpVector(int loacation, Vector3f vector) {
		GL20.glUniform3f(loacation, vector.x, vector.y, vector.z);
	}

	protected void loadUp4DVector(int loacation, Vector4f vector) {
		GL20.glUniform4f(loacation, vector.x, vector.y, vector.z, vector.w);
	}

	protected void loadUp2DVector(int loacation, Vector2f vector) {
		GL20.glUniform2f(loacation, vector.x, vector.y);
	}

	protected void loadUpBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}

	protected void loadUpMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}

	private static ShaderHolder loadShader(String file, int type) {
		StringBuilder shaderSrc = new StringBuilder();
		List<String> uniforms = new ArrayList<>();
		try {
			InputStream in = Class.class.getResourceAsStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSrc.append(line).append("\n");
				if(line.toLowerCase().startsWith("uniform")){
					uniforms.add(line);
				}
			}
			reader.close();
		} catch (Exception e) {
			//Main.logger.logWithMsgbox("Error while reading a shader: " + file + " Error: " + e, true);
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSrc);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			//Main.logger.logWithMsgbox("Error while compiling a shader: " + file, true);
			//Main.logger.logDef(GL20.glGetShaderInfoLog(shaderID, 500), true);
		}
		return new ShaderHolder(shaderID, uniforms);
	}
	
	private static class ShaderHolder{
		private List<String> uniforms;
		private int id;
		
		private ShaderHolder(int id, List<String> uniformLines){
			this.uniforms = uniformLines;
			this.id = id;
		}
		
		private int getID(){
			return id;
		}
		
		private List<String> getUniformLines(){
			return uniforms;
		}
	}
}
