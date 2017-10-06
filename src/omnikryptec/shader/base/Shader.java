package omnikryptec.shader.base;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import omnikryptec.gameobject.Entity;
import omnikryptec.graphics.OpenGL;
import omnikryptec.main.AbstractScene;
import omnikryptec.main.OmniKryptecEngine.ShutdownOption;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.Instance;
import omnikryptec.util.exceptions.OmniKryptecException;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class Shader {

	private static final String MODULE_LOCATION = "omnikryptec/shader/modules/";
	private static final String MODULE_PREFIX = "#module";
	private static final String DYNAMIC_VAR_START = "$OKE_";
	private static final String DYNAMIC_VAR_END = "$";

	public static final String DEFAULT_PP_VERTEX_SHADER_POS_ATTR = "position";
	public static final String DEFAULT_PP_VERTEX_SHADER_TEXC_OUT = "textureCoords";

	protected static final AdvancedFile SHADER_LOCATION = new AdvancedFile("omnikryptec", "shader", "files");
	protected static final AdvancedFile SHADER_LOCATION_PP = new AdvancedFile("omnikryptec", "shader", "files",
			"postprocessing");
	protected static final AdvancedFile SHADER_LOCATION_RENDER = new AdvancedFile("omnikryptec", "shader", "files",
			"render");
	protected static final AdvancedFile DEF_SHADER_LOC_PP_VS = new AdvancedFile(SHADER_LOCATION_PP, "pp_vert.glsl");

	@Deprecated
	protected static final String oc_shader_loc = "/omnikryptec/shader/files/postprocessing";

	private static HashMap<String, String> loadedModules = new HashMap<>();

	private static int shadercount = 0;
	private static Shader shadercurrent;
	private static int shadercurrentid = -1;

	private static final List<Shader> allShader = new ArrayList<>();

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

	public Shader(AdvancedFile vertexFile, AdvancedFile fragmentFile, Object... uniAttr) {
		this((String) null, vertexFile, fragmentFile, uniAttr);
	}

	public Shader(String name, AdvancedFile vertexFile, AdvancedFile fragmentFile, Object... uniAttr) {
		this(name, vertexFile, null, fragmentFile, uniAttr);
	}

	public Shader(AdvancedFile vertexFile, AdvancedFile geometryFile, AdvancedFile fragmentFile, Object... uniAttr) {
		this((String) null, vertexFile, geometryFile, fragmentFile, uniAttr);
	}

	public Shader(String name, AdvancedFile vertexFile, AdvancedFile geometryFile, AdvancedFile fragmentFile,
			Object... uniAttr) {
		this(name, vertexFile == null ? null : vertexFile.createInputStream(),
				geometryFile == null ? null : geometryFile.createInputStream(),
				fragmentFile == null ? null : fragmentFile.createInputStream(), uniAttr);
	}

	public Shader(InputStream vertexFile, InputStream fragmentFile, Object... uniAttr) {
		this((String) null, vertexFile, fragmentFile, uniAttr);
	}

	public Shader(String name, InputStream vertexFile, InputStream fragmentFile, Object... uniAttr) {
		this(name, vertexFile, null, fragmentFile, uniAttr);
	}

	public Shader(InputStream vertexFile, InputStream geometryFile, InputStream fragmentFile, Object... uniAttr) {
		this((String) null, vertexFile, geometryFile, fragmentFile, uniAttr);
	}

	public Shader(String name, InputStream vertexFile, InputStream geometryFile, InputStream fragmentFile,
			Object... uniAttr) {
		if (name == null) {
			name = this.getClass().getSimpleName();// "" + shadercount;
		}
		name = "Shader " + shadercount + " (" + name + ")";
		this.name = name;
		vertexShaderHolder = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderHolder = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		vertexShaderID = vertexShaderHolder.getID();
		fragmentShaderID = fragmentShaderHolder.getID();
		programID = OpenGL.gl20createProgram();
		OpenGL.gl20attachShader(programID, vertexShaderID);
		if (geometryFile != null) {
			geometryShaderHolder = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER);
			geometryShaderID = geometryShaderHolder.getID();
			OpenGL.gl20attachShader(programID, geometryShaderID);
		}
		OpenGL.gl20attachShader(programID, fragmentShaderID);
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
		OpenGL.gl20linkProgram(programID);
		OpenGL.gl20validateProgram(programID);
		storeUniforms(uniformstmp.toArray(new Uniform[1]));
		shadercount++;
		allShader.add(this);
	}

	// ******************************************************************

	public void onRenderStart(AbstractScene s, Vector4f clipPlane) {

	}

	public void onModelRenderStart(AdvancedModel m) {

	}

	public void onRenderInstance(Entity e) {

	}

	public void onModelRenderEnd(AdvancedModel m) {

	}

	public void onRenderEnd(AbstractScene s) {

	}

	// ******************************************************************

	public int getId() {
		return programID;
	}

	public String getName() {
		return name;
	}

	public void registerUniforms(Uniform... uniformsarray) {
		registerUniformsA(uniformsarray);
	}

	protected void registerUniformsA(Uniform[] array, Uniform... uniformsarray) {
		storeUniforms(array);
		storeUniforms(uniformsarray);
	}

	public void start() {
		if (shadercurrentid != programID) {
			shadercurrent = this;
			OpenGL.gl20useProgram(programID);
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
		OpenGL.gl20useProgram(0);
	}

	public static void cleanAllShader() {
		for (Shader s : allShader) {
			s.cleanup();
		}
	}

	private void cleanup() {
		stop();
		OpenGL.gl20detachShader(programID, vertexShaderID);
		OpenGL.gl20detachShader(programID, fragmentShaderID);
		OpenGL.gl20deleteShader(vertexShaderID);
		OpenGL.gl20deleteShader(fragmentShaderID);
		if (geometryShaderID != 0) {
			OpenGL.gl20detachShader(programID, geometryShaderID);
			OpenGL.gl20deleteShader(geometryShaderID);
		}
		OpenGL.gl20deleteProgram(programID);
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
		OpenGL.gl20bindAttribLocation(programID, a.getIndex(), a.getName());
	}

	// ==============================================LOADINGSECTION=======================================================

	private String readShader(InputStream st, List<String> putUniformsHere) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(st))) {
			StringBuilder shaderSrc = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.toLowerCase().startsWith(MODULE_PREFIX)) {
					shaderSrc
							.append(
									getModuleString(line.toLowerCase().substring(MODULE_PREFIX.length()).trim()))
							.append("\n");
				} else {
					shaderSrc.append(line).append("\n");
					if (line.toLowerCase().trim().startsWith("uniform")) {
						putUniformsHere.add(insertDynamics(line));
					}
				}
			}
			reader.close();
			return insertDynamics(shaderSrc.toString());
		} catch (Exception e) {
			Logger.logErr("Failed to read a shader", e);
		}
		return "FAIL";
	}

	private static String getModuleString(String name) {
		String s = loadedModules.get(name);
		if (s == null) {
			if (name == null) {
				Logger.log("Could not find null-module", LogLevel.WARNING);
			}
			s = readModule(name);
			if (s == null) {
				Logger.log("Could not find module: " + name, LogLevel.WARNING);
			} else {
				loadedModules.put(name, s);
			}
		}
		return s;
	}

	private static String insertDynamics(String s) {
		String st = s;
		String word, value;
		while (s.contains(DYNAMIC_VAR_START)) {
			st = st.substring(st.indexOf(DYNAMIC_VAR_START));
			int i = st.indexOf(DYNAMIC_VAR_END, DYNAMIC_VAR_START.length());
			if(i>=0) {
				i++;
			}
			word = st.substring(0, (i <= -1 || i > st.length())? st.length() : i);
			value = getValueOf(word);
			if(value==null) {
				Logger.log("Keyword \"" + word + "\" not found!\nIn String:\n"+s, LogLevel.WARNING);
				break;
			}
			s = s.replace(word, value);
		}
		return s;
	}

	private static String getValueOf(String word) {
		word = word.replace(DYNAMIC_VAR_START, "");
		word = word.replace(DYNAMIC_VAR_END, "");
		switch (word) {
		case "MAX_LIGHTS":
			return Instance.getGameSettings().getLightMaxForward() + "";
		default:
			return null;
		}
	}

	private static String readModule(String name) {
		AdvancedFile ff = new AdvancedFile(MODULE_LOCATION, name);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(ff.createInputStream()))) {
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.toLowerCase().startsWith(MODULE_PREFIX)) {
					builder.append(getModuleString(line.toLowerCase().substring(MODULE_PREFIX.length()).trim()))
							.append("\n");
				} else {
					builder.append(line).append("\n");
				}
			}
			reader.close();
			return builder.toString();
		} catch (Exception e) {
			Logger.logErr("Failed to read module: " + name, e);
			return null;
		}
	}

	private ShaderHolder loadShader(InputStream in, int type) {
		List<String> uniforms = new ArrayList<>();

		String shaderSrc = readShader(in, uniforms);
		int shaderID = OpenGL.gl20createShader(type);
		OpenGL.gl20shaderSource(shaderID, shaderSrc);
		OpenGL.gl20compileShader(shaderID);
		if (OpenGL.gl20getShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Logger.logErr("Shader compilation failed in " + getShaderType(type) + ": " + name,
					new OmniKryptecException("Shadercreation"));
			Logger.log(OpenGL.gl20getShaderInfoLog(shaderID, 1024), LogLevel.ERROR, true);
			if(Logger.isDebugMode()) {
				Logger.log("SHADER-SRC:\n\n"+shaderSrc, LogLevel.INFO);
			}
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

}
