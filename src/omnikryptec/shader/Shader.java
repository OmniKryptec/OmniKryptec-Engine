package omnikryptec.shader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;

public class Shader {
	
	public static final String DEFAULT_PP_VERTEX_SHADER_LOC = "/omnikryptec/shader_files/pp_vert.glsl";
	public static final String DEFAULT_PP_VERTEX_SHADER_POS_ATTR = "position";
	public static final String DEFAULT_PP_VERTEX_SHADER_TEXC_OUT = "textureCoords";
	protected static final String oc_shader_loc = "/omnikryptec/shader_files/";

	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	private int geometryShaderID=0;
	private List<String> uniforms = new ArrayList<>();
	private ShaderHolder vertexShaderHolder;
	private ShaderHolder fragmentShaderHolder;
	private ShaderHolder geometryShaderHolder;
	
	// private static FloatBuffer matrixBuffer =
	// BufferUtils.createFloatBuffer(16);

	public Shader(InputStream vertexFile, InputStream fragmentFile, Object... uniAttr){
		this(vertexFile, null, fragmentFile, uniAttr);
	}
	
	
	public Shader(InputStream vertexFile, InputStream geometryFile, InputStream fragmentFile, Object... uniAttr) {
		vertexShaderHolder = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderHolder = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		vertexShaderID = vertexShaderHolder.getID();
		fragmentShaderID = fragmentShaderHolder.getID();
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		if(geometryFile!=null){
			geometryShaderHolder = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER);
			geometryShaderID = geometryShaderHolder.getID();
			GL20.glAttachShader(programID, geometryShaderID);
		}
		GL20.glAttachShader(programID, fragmentShaderID);
		List<Uniform> uniformstmp = new ArrayList<>();
		List<String> attributes = new ArrayList<>();
		for (int i = 0; i < uniAttr.length; i++) {
			if (uniAttr[i] instanceof Uniform) {
				uniformstmp.add((Uniform) uniAttr[i]);
			} else if (uniAttr[i] instanceof String) {
				attributes.add((String) uniAttr[i]);
			}
		}
		bindAttributes(attributes.toArray(new String[1]));
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		storeUniforms(uniformstmp.toArray(new Uniform[1]));
		String tmp;
		for (int i = 0; i < vertexShaderHolder.getUniformLines().size(); i++) {
			tmp = vertexShaderHolder.getUniformLines().get(i).split(" ")[2].replace(";", "");
			if (uniforms.contains(tmp)) {
				if(Logger.isDebugMode()){
					Logger.log("Uniform name already in use (vertexshader): " + tmp, LogLevel.WARNING, true);
				}
			} else {
				uniforms.add(tmp);
			}
		}
		for (int i = 0; i < fragmentShaderHolder.getUniformLines().size(); i++) {
			tmp = fragmentShaderHolder.getUniformLines().get(i).split(" ")[2].replace(";", "");
			if (uniforms.contains(tmp)) {
				if(Logger.isDebugMode()){
					Logger.log("Uniform name already in use (fragmentshader): " + tmp, LogLevel.WARNING, true);
				}
			} else {
				uniforms.add(tmp);
			}
		}
		if(geometryFile!=null){
			for (int i = 0; i < geometryShaderHolder.getUniformLines().size(); i++) {
				tmp = geometryShaderHolder.getUniformLines().get(i).split(" ")[2].replace(";", "");
				if (uniforms.contains(tmp)) {
					if(Logger.isDebugMode()){
						Logger.log("Uniform name already in use (geometryshader): " + tmp, LogLevel.WARNING, true);
					}
				} else {
					uniforms.add(tmp);
				}
			}
		}
//		if (uniformstmp.size() != uniforms.size() && Logger.isDebugMode()) {
//			Logger.log("Found uniforms: " + uniforms + ";\n		Required uniforms in constructor: " + uniformstmp.size(),
//					LogLevel.WARNING, false);
//		}
	}
	
	protected void registerUniforms(Uniform...uniformsarray){
		storeUniforms(uniformsarray);
		String tmp;
		for (int i = 0; i < vertexShaderHolder.getUniformLines().size(); i++) {
			tmp = vertexShaderHolder.getUniformLines().get(i).split(" ")[2].replace(";", "");
			if (uniforms.contains(tmp)) {
				if(Logger.isDebugMode()){
					Logger.log("Uniform name already in use (vertexshader): " + tmp, LogLevel.WARNING, true);
				}
			} else {
				uniforms.add(tmp);
			}
		}
		for (int i = 0; i < fragmentShaderHolder.getUniformLines().size(); i++) {
			tmp = fragmentShaderHolder.getUniformLines().get(i).split(" ")[2].replace(";", "");
			if (uniforms.contains(tmp)) {
				if(Logger.isDebugMode()){
					Logger.log("Uniform name already in use (fragmentshader): " + tmp, LogLevel.WARNING, true);
				}
			} else {
				uniforms.add(tmp);
			}
		}
		if(geometryShaderHolder!=null){
			for (int i = 0; i < geometryShaderHolder.getUniformLines().size(); i++) {
				tmp = geometryShaderHolder.getUniformLines().get(i).split(" ")[2].replace(";", "");
				if (uniforms.contains(tmp)) {
					if(Logger.isDebugMode()){
						Logger.log("Uniform name already in use (geometryshader): " + tmp, LogLevel.WARNING, true);
					}
				} else {
					uniforms.add(tmp);
				}
			}
		}
	}
	
	
	public void start() {
		GL20.glUseProgram(programID);
	}

	/**
	 * Works without "stopping" the shader.
	 */
	@Deprecated
	public void stop() {
		GL20.glUseProgram(0);
	}

	public void cleanup() {
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		if(geometryShaderID!=0){
			GL20.glDetachShader(programID, geometryShaderID);
			GL20.glDeleteShader(geometryShaderID);
		}
		GL20.glDeleteProgram(programID);
	}

	private void storeUniforms(Uniform... uniforms) {
		if(uniforms==null||uniforms.length==0){
			return;
		}
		for (int i = 0; i < uniforms.length; i++) {
			if(uniforms[i]!=null){
				uniforms[i].storeUniformLocation(programID);
			}
		}
	}

	private void bindAttributes(String... strings) {
		if(strings==null||strings.length==0){
			return;
		}
		for (int i = 0; i < strings.length; i++) {
			GL20.glBindAttribLocation(programID, i, strings[i]);
		}
	}

	public boolean testForUniform(String name) {
		return uniforms.contains(name);
	}

	// ==============================================LOADINGSECTION=======================================================

	private static ShaderHolder loadShader(InputStream in, int type) {
		StringBuilder shaderSrc = new StringBuilder();
		List<String> uniforms = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSrc.append(line).append("\n");
				if (line.toLowerCase().trim().startsWith("uniform")) {
					uniforms.add(line);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSrc);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Logger.log("Shader compilation failed: ", LogLevel.ERROR, true);
			Logger.log(GL20.glGetShaderInfoLog(shaderID, 500), LogLevel.ERROR, true);
		}
		return new ShaderHolder(shaderID, uniforms);
	}

	private static class ShaderHolder {
		private List<String> uniforms;
		private int id;

		private ShaderHolder(int id, List<String> uniformLines) {
			this.uniforms = uniformLines;
			this.id = id;
		}

		private int getID() {
			return id;
		}

		private List<String> getUniformLines() {
			return uniforms;
		}
	}

	// public int getUniformID(String name) {
	// return uniforms_map.get(name);
	// }

	// private int getUniformLocation(String uniformName) {
	// return GL20.glGetUniformLocation(programID, uniformName);
	// }

	// protected void bindAttribute(int attrib, String varName) {
	// GL20.glBindAttribLocation(programID, attrib, varName);
	// }

	// public void loadFloat(String name, float value) {
	// errorIfNoSuchUniform(name);
	// loadFloat(uniforms_map.get(name), value);
	// }
	//
	// public void loadFloat(int location, float value) {
	// GL20.glUniform1f(location, value);
	// }
	//
	// public void loadInt(String name, int value) {
	// errorIfNoSuchUniform(name);
	// loadInt(uniforms_map.get(name), value);
	// }
	//
	// public void loadInt(int location, int value) {
	// GL20.glUniform1i(location, value);
	// }
	//
	// public void loadVector3f(String name, Vector3f vector) {
	// errorIfNoSuchUniform(name);
	// loadVector3f(uniforms_map.get(name), vector);
	// }
	//
	// public void loadVector3f(int loacation, Vector3f vector) {
	// GL20.glUniform3f(loacation, vector.x, vector.y, vector.z);
	// }
	//
	// public void loadVector4f(String name, Vector4f vector) {
	// errorIfNoSuchUniform(name);
	// loadVector4f(uniforms_map.get(name), vector);
	// }
	//
	// public void loadVector4f(int loacation, Vector4f vector) {
	// GL20.glUniform4f(loacation, vector.x, vector.y, vector.z, vector.w);
	// }
	//
	// public void loadVector2f(String name, Vector2f vector) {
	// errorIfNoSuchUniform(name);
	// loadVector2f(uniforms_map.get(name), vector);
	// }
	//
	// public void loadVector2f(int loacation, Vector2f vector) {
	// GL20.glUniform2f(loacation, vector.x, vector.y);
	// }
	//
	// public void loadBoolean(String name, boolean value) {
	// errorIfNoSuchUniform(name);
	// loadBoolean(uniforms_map.get(name), value);
	// }
	//
	// public void loadBoolean(int location, boolean value) {
	// float toLoad = 0;
	// if (value) {
	// toLoad = 1;
	// }
	// GL20.glUniform1f(location, toLoad);
	// }
	//
	// public void loadMatrix4f(String name, Matrix4f matrix) {
	// errorIfNoSuchUniform(name);
	// loadMatrix4f(uniforms_map.get(name), matrix);
	// }
	//
	// public void loadMatrix4f(int location, Matrix4f matrix) {
	// matrix.store(matrixBuffer);
	// matrixBuffer.flip();
	// GL20.glUniformMatrix4(location, false, matrixBuffer);
	// }
	//
	// private void errorIfNoSuchUniform(String name) {
	// if (!testForUniform(name)) {
	// Logger.log("No such uniform: "+name, LogLevel.WARNING, true, true);
	// }
	// }

}
