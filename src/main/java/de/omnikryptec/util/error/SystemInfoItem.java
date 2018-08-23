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

package de.omnikryptec.util.error;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class SystemInfoItem implements ErrorItem{

	@Override
	public String getError() {
		StringBuilder b = new StringBuilder();
		try{
			b.append("OpenGL-Vendor: ").append(GL11.glGetString(GL11.GL_VENDOR)).append("\n");
			b.append("OpenGL-Version: ").append(GL11.glGetString(GL11.GL_VERSION)).append("\n");
			b.append("Renderer: ").append(GL11.glGetString(GL11.GL_RENDERER)).append("\n");
			b.append("GLFW: ").append(GLFW.glfwGetVersionString()).append("\n");
		}catch(Exception e){
			b.append("No OpenGL information available.").append("\n");
		}
		b.append("\n");
		b.append("Java-Version: ").append(System.getProperty("java.version"));
		b.append("\n");
		return b.toString();
	}

}
