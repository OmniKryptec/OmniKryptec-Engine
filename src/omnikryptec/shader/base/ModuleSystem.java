package omnikryptec.shader.base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import omnikryptec.util.AdvancedFile;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class ModuleSystem {
	private static final String PRIVATE_MODULE = "@m";

	private String MODULE_LOCATION;
	private String MODULE_PREFIX;
	private String DYNAMIC_VAR_START;
	private String DYNAMIC_VAR_END;
	private String EXTERN_MODULE_PREFIX;
	private String MODULE_FILE_SUFFIX;
	
	private HashMap<String, String> loadedModules = new HashMap<>();
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
	
	public String processLine(String line) {
		if(line.startsWith(MODULE_PREFIX)) {
			line = getModuleString(line.substring(MODULE_PREFIX.length()).trim());
		}
		return insertDynamics(line);
	}
	
	public String getModuleString(String name) {
		return getModuleString(name, false);
	}
	
	private String getModuleString(String name, boolean access) {
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
				if(s.contains(PRIVATE_MODULE)&&!access) {
					Logger.log("Module is only for other modules: " + name, LogLevel.WARNING);
					return null;
				}
			}
		}
		return s;
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
//		switch (word) {
//		case "MAX_LIGHTS":
//			return Instance.getGameSettings().getLightMaxForward() + "";
//		case "MAX_JOINTS":
//			return Instance.getGameSettings().getInteger(GameSettings.ANIMATION_MAX_JOINTS)+"";
//		case "MAX_WEIGHTS":
//			return Instance.getGameSettings().getInteger(GameSettings.ANIMATION_MAX_WEIGHTS)+"";
//		default:
//			return null;
//		}
	}

	private String readModule(String name) {
		AdvancedFile ff;
		if(!name.endsWith(MODULE_FILE_SUFFIX)) {
			name += MODULE_FILE_SUFFIX;
		}
		if (name.startsWith(EXTERN_MODULE_PREFIX)) {
			ff = new AdvancedFile(name.substring(EXTERN_MODULE_PREFIX.length()));
		} else {
			ff = new AdvancedFile(MODULE_LOCATION, name);
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(ff.createInputStream()))) {
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.toLowerCase().startsWith(MODULE_PREFIX)) {
					builder.append(getModuleString(line.toLowerCase().substring(MODULE_PREFIX.length()).trim()).replace(PRIVATE_MODULE, ""))
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
}
