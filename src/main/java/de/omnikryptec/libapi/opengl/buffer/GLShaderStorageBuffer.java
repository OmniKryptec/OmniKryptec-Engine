package de.omnikryptec.libapi.opengl.buffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.opengl.OpenGLUtil;

public class GLShaderStorageBuffer extends GLBuffer {

    public GLShaderStorageBuffer() {
        super(GL43.GL_SHADER_STORAGE_BUFFER);
    }

    public void setDescription(BufferUsage usage, Type type, int size, int index) {
        bindBuffer();
        GL15.glBufferData(bufferType(), size * OpenGLUtil.sizeof(type), OpenGLUtil.bufferUsageId(usage));
        GL30.glBindBufferBase(bufferType(), index, bufferId());
    }

    public void updateData(IntBuffer data) {
        data.flip();
        bindBuffer();
        GL15.glBufferSubData(bufferType(), 0, data);
    }

    public void updateData(FloatBuffer data) {
        data.flip();
        bindBuffer();
        GL15.glBufferSubData(bufferType(), 0, data);
    }
}
