package omnikryptec.shader.modules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import omnikryptec.util.AdvancedFile;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class ModuleSystem {
	static final String PRIVATE_MODULE = "@m";
	static final String DUPLICATE_ALLOWED = "@a";
	
	String MODULE_LOCATION;
	String MODULE_PREFIX;
	String DYNAMIC_VAR_START;
	String DYNAMIC_VAR_END;
	String EXTERN_MODULE_PREFIX;
	String MODULE_FILE_SUFFIX;
	
	private HashMap<String, Module> loadedModules = new HashMap<>();
	private HashMap<String, DynamicAccess<?>> dynamics = new HashMap<>();
	
	
	public ModuleSystem(String dynamicvarstart, String dynamicvarend, String defaultmoduleloc) {
		this(dynamicvarstart, dynamicvarend, defaultmoduleloc, "#module", "?", ".module");
	}
	
	public ModuleSystem(String dynamicvarstart, String dynamicvarend, String defaultmoduleloc, String moduleprefix, String externpathprefix, String modulefilesuffix) {
		this.DYNAMIC_VAR_START = dynamicvarstart;
		this.DYNAMIC_VAR_END = dynamicvarend;
		this.MODULE_LOCATION = defaultmoduleloc;
		this.MODULE_PREFIX = moduleprefix;
		this.EXTERN_MODULE_PREFIX = externpathprefix;
		this.MODULE_FILE_SUFFIX = modulefilesuffix;
	}
	
	public String compute(String s) {
		ArrayList<String> alreadyInstalled = new ArrayList<>();
		String[] lines = s.split("\n");
		StringBuilder strg = new StringBuilder();
		for(String line : lines) {
			if (line.startsWith(MODULE_PREFIX)) {
				String name = line.substring(MODULE_PREFIX.length()).trim();
				if(!alreadyInstalled.contains(name)) {
					Module m = getModule(name, false);
					strg.append(m.getComputedString(alreadyInstalled));
					if(!m.isDuplicateAllowed()) {
						alreadyInstalled.add(name);
					}
				}
			}else {
				strg.append(line).append("\n");
			}
		}
		return insertDynamics(strg.toString());
	}
	
	Module getModule(String name, boolean access) {
		Module s = loadedModules.get(name);
		if (s == null) {
			if (name == null) {
				Logger.log("Could not find null-module", LogLevel.WARNING);
			}
			s = readModule(name);
			if (!s.isFound()) {
				Logger.log("Could not find module: " + name, LogLevel.WARNING);
			} else {
				loadedModules.put(name, s);
				if(s.isPrivate()&!access) {
					Logger.log("Module is only for other modules: " + name, LogLevel.WARNING);
					return null;
				}
			}
		}
		return s;
	}

	private Module readModule(String name) {
		AdvancedFile ff;
		if(!name.endsWith(MODULE_FILE_SUFFIX)) {
			name += MODULE_FILE_SUFFIX;
		}
		if (name.startsWith(EXTERN_MODULE_PREFIX)) {
			ff = new AdvancedFile(name.substring(EXTERN_MODULE_PREFIX.length()));
		} else {
			ff = new AdvancedFile(MODULE_LOCATION, name);
		}
		return new Module(name, ff.createInputStream(), this);
	}
	
	public String insertDynamics(String s) {
		String st = s;
		String word, value;
		while (s.contains(DYNAMIC_VAR_START)) {
			st = st.substring(st.indexOf(DYNAMIC_VAR_START));
			int i = st.indexOf(DYNAMIC_VAR_END, DYNAMIC_VAR_START.length());
			if (i >= 0) {
				i++;
			}
			word = st.substring(0, (i <= -1 || i > st.length()) ? st.length() : i);
			value = getValueOf(word);
			if (value == null) {
				Logger.log("Keyword \"" + word + "\" not found!\nIn String:\n" + s, LogLevel.WARNING);
				break;
			}
			s = s.replace(word, value);
			st = st.substring(word.length());
		}
		return s;
	}

	
	public ModuleSystem addDynamic(String key, DynamicAccess<?> valuegetter) {
		dynamics.put(key, valuegetter);
		return this;
	}
	
	public String getValueOf(String word) {
		word = word.replace(DYNAMIC_VAR_START, "");
		word = word.replace(DYNAMIC_VAR_END, "");
		return dynamics.get(word).get()+"";
	}
}
