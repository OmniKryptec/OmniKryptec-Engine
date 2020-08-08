/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.libapi.opengl.shader;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;

import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.ShaderSource;
import de.omnikryptec.libapi.exposed.render.shader.Uniform;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.resource.parser.shader.ShaderParser.ShaderType;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Logger.LogType;

public class GLShader implements Shader, Deletable {
    
    private static final Logger logger = Logger.getLogger(GLShader.class);
    private static int count = 0;
    
    public static int getCount() {
        return count;
    }
    
    private final int programId;
    private final Map<ShaderType, Integer> attachments;
    private final Map<String, GLUniform> uniforms;
    
    public GLShader() {
        this.programId = GL20.glCreateProgram();
        this.attachments = new EnumMap<>(ShaderType.class);
        this.uniforms = new HashMap<>();
        registerThisAsAutodeletable();
    }
    
    @Override
    public void bindShader() {
        OpenGLUtil.useProgram(this.programId);
    }
    
    @Override
    public void deleteRaw() {
        for (final Integer id : this.attachments.values()) {
            GL20.glDetachShader(this.programId, id);
            GL20.glDeleteShader(id);
        }
        GL20.glDeleteProgram(this.programId);
        count--;
    }
    
    @Override
    public void create(final ShaderSource... shaderAttachments) {
        for (final ShaderSource a : shaderAttachments) {
            final int shader = GL20.glCreateShader(OpenGLUtil.shaderTypeId(a.shaderType));
            GL20.glShaderSource(shader, a.source);
            GL20.glCompileShader(shader);
            if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                String glerr = GL20.glGetShaderInfoLog(shader);
                String n = glerr.substring(glerr.indexOf("(") + 1, glerr.indexOf(")"));
                int line = Integer.parseInt(n);
                String[] lines = a.source.split("\n");
                logger.log(LogType.Error, "Compilation error", "Shader: " + a.context + " (" + a.shaderType + ")",
                        "Error: " + glerr, LogType.Debug, "Src: \n" + lines[line]);
            } else {
                GL20.glAttachShader(this.programId, shader);
            }
            this.attachments.put(a.shaderType, shader);
        }
        GL20.glLinkProgram(this.programId);
        GL20.glValidateProgram(this.programId);
        for (final ShaderSource a : shaderAttachments) {
            extractUniforms(a.source);
        }
        count++;
    }
    
    @Override
    public <T extends Uniform> T getUniform(final String name) {
        final Uniform u = this.uniforms.get(name);
        if (u == null) {
            throw new IllegalArgumentException("No uniform with name " + name + " in this shader");
        }
        return (T) u;
    }
    
    //TODO tmp
    public int progResInd(String s) {
        return GL43.glGetProgramResourceIndex(this.programId, GL43.GL_SHADER_STORAGE_BLOCK, s);
    }
    
    public void dispatchCompute(final int xCount, final int yCount, final int zCount) {
        GL43.glDispatchCompute(xCount, yCount, zCount);
    }
    
    //TODx somewhere else? no, because this is probably shader dependant
    private void extractUniforms(final String src) {
        final String[] lines = src.split("[\n\r]+");
        for (final String l : lines) {
            if (l.contains("uniform")) {
                final String un = l.substring(l.indexOf("uniform") + "uniform".length()).replace(";", "").trim();
                final String[] data = un.split("\\s+");
                final String name = data[1].trim();
                final String types = data[0].trim();
                final GLUniform unif = createUniformObj(name, types);
                unif.storeUniformLocation(this.programId);
                this.uniforms.put(name, unif);
            }
        }
    }
    
    //TODx better way of doing the uniforms? shader dependant so no
    private GLUniform createUniformObj(final String name, final String types) {
        switch (types) {
        case "mat4":
            return new GLUniformMatrix(name);
        case "sampler2D":
        case "samplerCube":
            return new GLUniformSampler(name);
        case "vec4":
            return new GLUniformVec4(name);
        case "float":
            return new GLUniformFloat(name);
        case "image2D":
            return new GLUniformImage2D(name);
        case "vec3":
            return new GLUniformVec3(name);
        case "vec2":
            return new GLUniformVec2(name);
        case "int":
            return new GLUniformInt(name);
        default:
            throw new IllegalArgumentException("Uniform type not found: " + types + " " + name);
        }
    }
    
}
