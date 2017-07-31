package omnikryptec.shader.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class ShaderPack<T extends Shader> {

	private HashMap<String, Shader> pack;
	protected T defaultShader;
	
	public ShaderPack(T defaults, Object...os){
		this.defaultShader = defaults;
		final List<String> strings = new ArrayList<>();
		final List<Shader> shader = new ArrayList<>();
		for(Object o : os){
			if(o instanceof String){
				strings.add((String)o);
			}else if(o instanceof Shader){
				shader.add((Shader)o);
			}
		}
		pack = new HashMap<>();
		for(int i=0; i<strings.size(); i++){
			if(strings.get(i)!=null&&strings.get(i).length()>0&&i<shader.size()&&shader.get(i)!=null){
				pack.put(strings.get(i), shader.get(i));
			}
		}
		if(strings.size()==0){
			Logger.log("No shader(names) found!", LogLevel.WARNING); 
		}
	}
	
	public ShaderPack(T defaults){
		this(defaults, new HashMap<>());
	}
	
	public ShaderPack(T defaults, HashMap<String, Shader> pack){
		this.pack = pack;
		this.defaultShader = defaults;
	}
	
	public ShaderPack<T> addShader(String renderPassName, Shader shader){
		if(renderPassName==null||renderPassName.length()==0||shader==null){
			Logger.log("renderPassName or the shader is invalid!", LogLevel.WARNING);
			return this;
		}
		pack.put(renderPassName, shader);
		return this;
	}
	
	public ShaderPack<T> removeShader(String name){
		pack.remove(name);
		return this;
	}
	
	public Shader getShader(String renderPassName){
		Shader sh = pack.get(renderPassName);
		if(sh==null&&defaultShader!=null){
			sh = this.defaultShader;
		}
		return sh;
	}
	
	public ShaderPack<T> setDefaultShader(T s){
		this.defaultShader = s;
		return this;
	}
	
	public T getDefaultShader(){
		return defaultShader;
	}

}
