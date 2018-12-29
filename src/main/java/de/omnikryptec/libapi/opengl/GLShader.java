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
        for (Integer id : attachments.values()) {
            GL20.glDetachShader(programId, id);
            GL20.glDeleteShader(id);
        }
        GL20.glDeleteProgram(this.programId);
    }
    
    @Override
    public void create(ShaderAttachment... shaderAttachments) {
        for (ShaderAttachment a : shaderAttachments) {
            int shader = GL20.glCreateShader(OpenGLUtil.typeId(a.shaderType));
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
                GL20.glAttachShader(programId, shader);
            }
            attachments.put(a.shaderType, shader);
        }
        GL20.glLinkProgram(programId);
        GL20.glValidateProgram(programId);
    }
    
}
