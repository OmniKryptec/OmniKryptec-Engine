/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import de.codemakers.base.logger.LogLevel;
import de.omnikryptec.libapi.exposed.AutoDelete;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.ShaderSource;
import de.omnikryptec.libapi.exposed.render.shader.Uniform;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.resource.parser.shader.ShaderParser.ShaderType;
import de.omnikryptec.util.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class GLShader extends AutoDelete implements Shader {
    
    private final int programId;
    private final Map<ShaderType, Integer> attachments;
    private final Map<String, GLUniform> uniforms;
    
    public GLShader() {
        this.programId = GL20.glCreateProgram();
        this.attachments = new EnumMap<>(ShaderType.class);
        this.uniforms = new HashMap<>();
    }
    
    @Override
    public void bindShader() {
        OpenGLUtil.useProgram(this.programId);
    }
    
    @Override
    protected void deleteRaw() {
        for (final Integer id : this.attachments.values()) {
            GL20.glDetachShader(this.programId, id);
            GL20.glDeleteShader(id);
        }
        GL20.glDeleteProgram(this.programId);
    }
    
    @Override
    public void create(final ShaderSource... shaderAttachments) {
        for (final ShaderSource a : shaderAttachments) {
            final int shader = GL20.glCreateShader(OpenGLUtil.typeId(a.shaderType));
            GL20.glShaderSource(shader, a.source);
            GL20.glCompileShader(shader);
            if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                Logger.logError(GLShader.class, "Compilation error", "Shader: " + a.shaderType, "Error: " + GL20.glGetShaderInfoLog(shader), LogLevel.DEBUG, "Src: \n" + a.source);
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
    }
    
    @Override
    public <T extends Uniform> T getUniform(final String name) {
        final Uniform u = this.uniforms.get(name);
        if (u == null) {
            throw new IllegalArgumentException("No uniform with name " + name + " in this shader");
        }
        return (T) u;
    }
    
    //TODO somewhere else?
    private void extractUniforms(final String src) {
        final String[] lines = src.split("[\n\r]+");
        for (final String l : lines) {
            if (l.contains("uniform")) {
                try {
                    final String un = l.substring(l.indexOf("uniform") + "uniform".length()).replace(";", "").trim();
                    final String[] data = un.split("\\s+");
                    final String name = data[1].trim();
                    final String types = data[0].trim();
                    final GLUniform unif = createUniformObj(name, types);
                    unif.storeUniformLocation(this.programId);
                    this.uniforms.put(name, unif);
                } catch (final Exception ex) {
                    System.err.println("Couldn't handle uniform: " + l);
                    ex.printStackTrace();
                }
            }
        }
    }
    
    //TODO better way of doing the uniforms?
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
            default:
                throw new IllegalArgumentException("Type not found: " + types + " " + name);
        }
    }
    
}
