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

import de.omnikryptec.old.util.logger.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Module {
	private String name;
	private String raw;
	private ArrayList<String> submodules = new ArrayList<>();
	private ArrayList<String> header = new ArrayList<>();
	private ModuleSystem sys;
	
	public Module(String name, InputStream stream, ModuleSystem sys) {
		this.sys = sys;
		this.name = name;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(sys.MODULE_PREFIX)) {
					submodules.add(line.substring(sys.MODULE_PREFIX.length()).trim());
				} 
				builder.append(line).append("\n");
			}
			reader.close();
			raw = builder.toString();
			int headerstart = raw.indexOf(sys.HEADERSECTION_START);
			int headerend = raw.indexOf(sys.HEADERSECTION_END);
			if(headerstart!=-1&&headerend!=-1) {
				String header = raw.substring(headerstart+sys.HEADERSECTION_START.length(), headerend);
				raw = raw.replace(sys.HEADERSECTION_START, "").replace(sys.HEADERSECTION_END, "");
				String[] array = header.split("\n");
				for(String s : array) {
					s = s.trim();
					if(!s.isEmpty()&&!s.equals("\n")) {
						this.header.add(s);
					}
				}
				raw = raw.replace(header, "");
			}
		} catch (Exception e) {
			Logger.logErr("Failed to read module: " + name, e);
			raw = null;
		}
	}
	
	public ArrayList<String> getHeader(){
		return header;
	}
	
	public String getComputedString(ArrayList<String> alreadyinstalled) {
		if(!isFound()) {
			return "";
		}
		String[] lines = raw.split("\n");
		StringBuilder builder = new StringBuilder();
		for(String line : lines) {
			if (line.trim().startsWith(sys.MODULE_PREFIX)) {
				String name = line.substring(sys.MODULE_PREFIX.length()).trim();
				if(!alreadyinstalled.contains(name)) {
					Module m = sys.getModule(name, true);
					if(!m.isDuplicateAllowed()) {
						alreadyinstalled.add(name);
					}
					builder.append(m.getComputedString(alreadyinstalled));
				}
			} else {
				builder.append(line).append("\n");
			}
		}
		return builder.toString();
	}
	
	public ArrayList<String> getSubModules(){
		return submodules;
	}
	
	public boolean isPrivate() {
		return raw.contains(ModuleSystem.PRIVATE_MODULE);
	}
	
	public boolean isDuplicateAllowed() {
		return raw.contains(ModuleSystem.DUPLICATE_ALLOWED);
	}
	
	public boolean isFound() {
		return raw != null;
	}
	
	public String getName() {
		return name;
	}
}
