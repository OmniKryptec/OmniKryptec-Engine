package omnikryptec.shader.base;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import omnikryptec.main.OmniKryptecEngine.ShutdownOption;
import omnikryptec.util.Instance;
import omnikryptec.util.exceptions.OmniKryptecException;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogLevel;

public class Shader {

	public static final String DEFAULT_PP_VERTEX_SHADER_LOC = "/omnikryptec/shader/files/pp_vert.glsl";
	public static final String DEFAULT_PP_VERTEX_SHADER_POS_ATTR = "position";
	public static final String DEFAULT_PP_VERTEX_SHADER_TEXC_OUT = "textureCoords";
	protected static final String oc_shader_loc = "/omnikryptec/shader/files/";

	private static int shadercount = 0;
	private static Shader shadercurrent;
	private static int shadercurrentid = -1;

	public static Shader getActiveShader() {
		return shadercurrent;
	}

	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	private int geometryShaderID = 0;
	private ShaderHolder vertexShaderHolder;
	private ShaderHolder fragmentShaderHolder;
	private ShaderHolder geometryShaderHolder;
	private String name;
	private LineInsert insert;

	// private static FloatBuffer matrixBuffer =
	// BufferUtils.createFloatBuffer(16);

	public Shader(InputStream vertexFile, InputStream fragmentFile, Object... uniAttr) {
		this((String) null, (LineInsert) null, vertexFile, fragmentFile, uniAttr);
	}

	public Shader(LineInsert insert, InputStream vertexFile, InputStream fragmentFile, Object... uniAttr) {
		this((String) null, insert, vertexFile, fragmentFile, uniAttr);
	}

	public Shader(String name, InputStream vertexFile, InputStream fragmentFile, Object... uniAttr) {
		this(name, (LineInsert) null, vertexFile, null, fragmentFile, uniAttr);
	}

	public Shader(String name, LineInsert insert, InputStream vertexFile, InputStream fragmentFile, Object... uniAttr) {
		this(name, insert, vertexFile, null, fragmentFile, uniAttr);
	}

	public Shader(InputStream vertexFile, InputStream geometryFile, InputStream fragmentFile, Object... uniAttr) {
		this((String) null, (LineInsert) null, vertexFile, geometryFile, fragmentFile, uniAttr);
	}

	public Shader(LineInsert insert, InputStream vertexFile, InputStream geometryFile, InputStream fragmentFile,
			Object... uniAttr) {
		this(null, insert, vertexFile, geometryFile, fragmentFile, uniAttr);
	}

	public Shader(String name, InputStream vertexFile, InputStream geometryFile, InputStream fragmentFile,
			Object... uniAttr) {
		this(name, (LineInsert) null, vertexFile, geometryFile, fragmentFile, uniAttr);
	}

	public Shader(String name, LineInsert insert, InputStream vertexFile, InputStream geometryFile,
			InputStream fragmentFile, Object... uniAttr) {
		if (name == null) {
			name = "" + shadercount;
		}
		name = "Shader " + name;
		this.insert = insert;
		this.name = name;
		vertexShaderHolder = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderHolder = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		vertexShaderID = vertexShaderHolder.getID();
		fragmentShaderID = fragmentShaderHolder.getID();
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		if (geometryFile != null) {
			geometryShaderHolder = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER);
			geometryShaderID = geometryShaderHolder.getID();
			GL20.glAttachShader(programID, geometryShaderID);
		}
		GL20.glAttachShader(programID, fragmentShaderID);
		List<Uniform> uniformstmp = new ArrayList<>();
		List<Attribute> attributes = new ArrayList<>();
		for (int i = 0; i < uniAttr.length; i++) {
			if (uniAttr[i] instanceof Uniform) {
				uniformstmp.add((Uniform) uniAttr[i]);
			} else if (uniAttr[i] instanceof Attribute) {
				attributes.add((Attribute) uniAttr[i]);
			} else if (uniAttr[i] instanceof String) {
				attributes.add(new Attribute((String) uniAttr[i], i - uniformstmp.size()));
			}
		}
		bindAttributes(attributes.toArray(new Attribute[1]));
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		storeUniforms(uniformstmp.toArray(new Uniform[1]));
		shadercount++;
		// if (uniformstmp.size() != uniforms.size() && Logger.isDebugMode()) {
		// Logger.log("Found uniforms: " + uniforms + ";\n Required uniforms in
		// constructor: " + uniformstmp.size(),
		// LogLevel.WARNING, false);
		// }
	}

	public int getId() {
		return programID;
	}

	public String getName() {
		return name;
	}

	protected void registerUniforms(Uniform... uniformsarray) {
		registerUniformsa(uniformsarray);
	}

	protected void registerUniformsa(Uniform[] array, Uniform... uniformsarray) {
		storeUniforms(array);
		storeUniforms(uniformsarray);
	}

	public void start() {
		if (shadercurrentid != programID) {
			shadercurrent = this;
			GL20.glUseProgram(programID);
			shadercurrentid = programID;
		}
	}

	/**
	 * Works without "stopping" the shader.
	 */
	@Deprecated
	public void stop() {
		shadercurrent = null;
		shadercurrentid = 0;
		GL20.glUseProgram(0);
	}

	public void cleanup() {
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		if (geometryShaderID != 0) {
			GL20.glDetachShader(programID, geometryShaderID);
			GL20.glDeleteShader(geometryShaderID);
		}
		GL20.glDeleteProgram(programID);
	}

	private void storeUniforms(Uniform... uniforms) {
		if (uniforms == null || uniforms.length == 0) {
			return;
		}
		for (int i = 0; i < uniforms.length; i++) {
			if (uniforms[i] != null) {
				uniforms[i].storeUniformLocation(this);
			}
		}
	}

	private void bindAttributes(Attribute... strings) {
		if (strings == null || strings.length == 0) {
			return;
		}
		for (int i = 0; i < strings.length; i++) {
			bindAttributeManually(strings[i]);
		}
	}

	private void bindAttributeManually(Attribute a) {
		GL20.glBindAttribLocation(programID, a.getIndex(), a.getName());
	}

	// ==============================================LOADINGSECTION=======================================================

	private ShaderHolder loadShader(InputStream in, int type) {
		StringBuilder shaderSrc = new StringBuilder();
		List<String> uniforms = new ArrayList<>();
		int linenr = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				if (insert != null && linenr == 1) {
					String[] lines = insert.get(type);
					if (lines != null) {
						for (int i = 0; i < lines.length; i++) {
							shaderSrc.append(lines[i]).append("\n");
							linenr++;
						}
					}
				}
				shaderSrc.append(line).append("\n");
				if (line.toLowerCase().trim().startsWith("uniform")) {
					uniforms.add(line);
				}
				linenr++;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSrc);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Instance.getEngine().errorOccured(new OmniKryptecException("Shadercreation"), "Shader compilation failed in Shader: " + name);
			Logger.log("(Lines with LineInsertion!): " + GL20.glGetShaderInfoLog(shaderID, 1024), LogLevel.ERROR, true);
			Instance.getEngine().close(ShutdownOption.JAVA);
		}
		return new ShaderHolder(shaderID, uniforms, type);
	}

	public static String getShaderType(int i) {
		switch (i) {
		case GL20.GL_FRAGMENT_SHADER:
			return "fragmentshader";
		case GL20.GL_VERTEX_SHADER:
			return "vertexshader";
		case GL32.GL_GEOMETRY_SHADER:
			return "geometryshader";
		default:
			return "unknown_shadertype";
		}
	}

	private class ShaderHolder {
		private int id;

		private ShaderHolder(int id, List<String> uniformLines, int type) {
			List<String> tmplist = new ArrayList<>();
			for (int i = 0; i < uniformLines.size(); i++) {
				if (tmplist.contains(uniformLines.get(i))) {
					if (Logger.isDebugMode()) {
						Logger.log(name + ": Uniform name already in use (" + getShaderType(type) + "): "
								+ uniformLines.get(i), LogLevel.WARNING, true);
					}
				} else {
					tmplist.add(uniformLines.get(i));
				}
			}
			this.id = id;
		}

		private int getID() {
			return id;
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
