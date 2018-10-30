/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.shader.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

public class ModuleSystem {

    static final String PRIVATE_MODULE = "@m";
    static final String DUPLICATE_ALLOWED = "@a";

    String HEADERSECTION_START;
    String HEADERSECTION_END;
    String MODULE_LOCATION;
    String MODULE_PREFIX;
    String DYNAMIC_VAR_START;
    String DYNAMIC_VAR_END;
    String EXTERN_MODULE_PREFIX;
    String MODULE_FILE_SUFFIX;
    String INSERT_HEADERS_AFTER_LINE;

    private HashMap<String, Module> loadedModules = new HashMap<>();
    private HashMap<String, Supplier<?>> dynamics = new HashMap<>();

    public ModuleSystem(String dynamicvarstart, String dynamicvarend, String defaultmoduleloc) {
	this(dynamicvarstart, dynamicvarend, defaultmoduleloc, "#module", "?", ".module");
    }

    public ModuleSystem(String dynamicvarstart, String dynamicvarend, String defaultmoduleloc, String moduleprefix,
	    String externpathprefix, String modulefilesuffix) {
	this(dynamicvarstart, dynamicvarend, defaultmoduleloc, moduleprefix, externpathprefix, modulefilesuffix,
		new HeaderDefinition("@h>", "@h<", "#version"));
    }

    public ModuleSystem(String dynamicvarstart, String dynamicvarend, String defaultmoduleloc, String moduleprefix,
	    String externpathprefix, String modulefilesuffix, HeaderDefinition headerdef) {
	this.DYNAMIC_VAR_START = dynamicvarstart;
	this.DYNAMIC_VAR_END = dynamicvarend;
	this.MODULE_LOCATION = defaultmoduleloc;
	this.MODULE_PREFIX = moduleprefix;
	this.EXTERN_MODULE_PREFIX = externpathprefix;
	this.MODULE_FILE_SUFFIX = modulefilesuffix;
	this.HEADERSECTION_START = headerdef.start;
	this.HEADERSECTION_END = headerdef.end;
	this.INSERT_HEADERS_AFTER_LINE = headerdef.inserthere;
    }

    public String compute(String s) {
	ArrayList<String> alreadyInstalled = new ArrayList<>();
	s = s.replace(HEADERSECTION_START, "");
	s = s.replace(HEADERSECTION_END, "");
	String[] lines = s.split("\n");
	StringBuilder strg = new StringBuilder();
	for (String line : lines) {
	    if (line.trim().startsWith(MODULE_PREFIX)) {
		String name = line.substring(MODULE_PREFIX.length()).trim();
		if (!alreadyInstalled.contains(name)) {
		    Module m = getModule(name, false);
		    if (!m.isDuplicateAllowed()) {
			alreadyInstalled.add(name);
		    }
		    strg.append(m.getComputedString(alreadyInstalled));
		}
	    } else {
		strg.append(line).append("\n");
	    }
	}
	String string = strg.toString().replace(DUPLICATE_ALLOWED, "").replace(PRIVATE_MODULE, "");
	string = insertHeader(string, alreadyInstalled);
	return insertDynamics(string);
    }

    private String insertHeader(String string, ArrayList<String> modules) {
	ArrayList<String> newlines = new ArrayList<>(Arrays.asList(string.split("\n")));
	for (int i = 0; i < newlines.size(); i++) {
	    if (newlines.get(i).contains(INSERT_HEADERS_AFTER_LINE)) {
		for (String s : modules) {
		    newlines.addAll(i + 1, getModule(s, true).getHeader());
		}
		break;
	    }
	}
	return newlines.stream().collect(Collectors.joining("\n"));
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
		if (s.isPrivate() && !access) {
		    Logger.log("Module is only for other modules: " + name, LogLevel.WARNING);
		    return null;
		}
	    }
	}
	return s;
    }

    private Module readModule(String name) {
	AdvancedFile ff;
	if (!name.endsWith(MODULE_FILE_SUFFIX)) {
	    name += MODULE_FILE_SUFFIX;
	}
	if (name.startsWith(EXTERN_MODULE_PREFIX)) {
	    ff = new AdvancedFile(true, name.substring(EXTERN_MODULE_PREFIX.length()));
	} else {
	    ff = new AdvancedFile(true, MODULE_LOCATION, name);
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

    public ModuleSystem addDynamic(String key, Supplier<?> valuegetter) {
	dynamics.put(key, valuegetter);
	return this;
    }

    public String getValueOf(String word) {
	word = word.replace(DYNAMIC_VAR_START, "");
	word = word.replace(DYNAMIC_VAR_END, "");
	return dynamics.get(word) == null ? "$$$Invalid dynamic$$$" : dynamics.get(word).get() + "";
    }
}
