package omnikryptec.shader.base;

import java.util.HashMap;

import omnikryptec.util.exceptions.OmniKryptecException;

public class ShaderGroup {
	
	private HashMap<Integer, Shader> shaders = new HashMap<>();
	
	
	public ShaderGroup(Shader lvl0) {
		addShader(0, lvl0);
	}
	
	public Shader getShaderForLvl(int lvl) {
		lvl = Math.abs(lvl);
		int start = lvl;
		Shader sh = shaders.get(lvl);
		while(sh==null) {
			lvl--;
			if(lvl<0) {
				throw new OmniKryptecException("No shader for lvl "+start+" found!");
			}
			sh = shaders.get(lvl);
		}
		return sh;
	}
	
	public ShaderGroup addShader(int lvl, Shader s) {
		shaders.put(lvl, s);
		return this;
	}
	
}
