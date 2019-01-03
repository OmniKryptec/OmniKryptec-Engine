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

package de.omnikryptec.libapi.opengl;

import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;
import de.omnikryptec.libapi.exposed.AutoDelete;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.Shader;

public class GLShader extends AutoDelete implements Shader {

    private final int programId;
    private final Map<ShaderType, Integer> attachments;

    public GLShader() {
        this.programId = GL20.glCreateProgram();
        this.attachments = new EnumMap<>(ShaderType.class);
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
    public void create(final ShaderAttachment... shaderAttachments) {
        for (final ShaderAttachment a : shaderAttachments) {
            final int shader = GL20.glCreateShader(OpenGLUtil.typeId(a.shaderType));
            GL20.glShaderSource(shader, a.source);
            GL20.glCompileShader(shader);
            if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.err.println("Compilation error");
                System.err.println("Shader: " + a.shaderType);
                System.err.println("Error: " + GL20.glGetShaderInfoLog(shader));
                if (LibAPIManager.active().debug()) {
                    System.err.println("Src: \n" + a.source);
                }
            } else {
                GL20.glAttachShader(this.programId, shader);
            }
            this.attachments.put(a.shaderType, shader);
        }
        GL20.glLinkProgram(this.programId);
        GL20.glValidateProgram(this.programId);
    }

}
