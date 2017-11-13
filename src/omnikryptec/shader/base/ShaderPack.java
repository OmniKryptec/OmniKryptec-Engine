package omnikryptec.shader.base;

import java.util.HashMap;

import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class ShaderPack {

	private HashMap<String, ShaderGroup> pack;
	protected ShaderGroup defaultShader;
	protected boolean defaultShaderIfError = true;

	public ShaderPack(ShaderGroup defaults) {
		this( new HashMap<>(), defaults);
	}

	public ShaderPack(HashMap<String, ShaderGroup> pack, ShaderGroup defaults) {
		this.pack = pack;
		this.defaultShader = defaults;
	}

	public ShaderPack addShader(String renderPassName, ShaderGroup shader) {
		if (renderPassName == null || renderPassName.length() == 0 || shader == null) {
			Logger.log("renderPassName or the shader is invalid!", LogLevel.WARNING);
			return this;
		}
		pack.put(renderPassName, shader);
		return this;
	}

	public ShaderPack removeShader(String name) {
		pack.remove(name);
		return this;
	}

	public Shader getShader(String renderPassName, int lvl) {
		if(renderPassName==null||renderPassName.isEmpty()) {
			return defaultShader.getShaderForLvl(lvl);
		}
		ShaderGroup sh = pack.get(renderPassName);
		if (sh == null && defaultShader != null && defaultShaderIfError) {
			sh = this.defaultShader;
		}
		return sh.getShaderForLvl(lvl);
	}

	public ShaderPack setDefaultShader(ShaderGroup s) {
		this.defaultShader = s;
		return this;
	}

	public Shader getDefaultShader() {
		return defaultShader.getShaderForLvl(0);
	}
	
	public ShaderGroup getDefaultShaderGroup() {
		return defaultShader;
	}
}
