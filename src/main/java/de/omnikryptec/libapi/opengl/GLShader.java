package de.omnikryptec.libapi.opengl;

import org.lwjgl.opengl.GL20;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;
import de.omnikryptec.libapi.exposed.AutoDelete;
import de.omnikryptec.libapi.exposed.render.Shader;

public class GLShader extends AutoDelete implements Shader {

    private final int programId;
    //private final Map<ShaderType, Integer> attachments;

    public GLShader() {
        this.programId = GL20.glCreateProgram();
        //this.attachments = new EnumMap<>(ShaderType.class);
    }

    @Override
    public void bindShader() {
        OpenGLUtil.useProgram(this.programId);
    }

    @Override
    public void attach(final ShaderType type, final String src) {
    }

    @Override
    protected void deleteRaw() {
        GL20.glDeleteProgram(this.programId);
    }

}
