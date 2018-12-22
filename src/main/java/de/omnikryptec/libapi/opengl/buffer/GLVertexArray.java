package de.omnikryptec.libapi.opengl.buffer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL30;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout.VertexBufferElement;
import de.omnikryptec.libapi.opengl.OpenGLUtil;

public class GLVertexArray implements VertexArray {
    
    private static final List<GLVertexArray> all = new ArrayList<>();
    
    static {
        LibAPIManager.registerResourceShutdownHooks(() -> cleanup());
    }
    
    private static void cleanup() {
        while (!all.isEmpty()) {
            all.get(0).deleteArray();
        }
    }
    
    private final int pointer;
    
    public GLVertexArray() {
        this.pointer = GL30.glGenVertexArrays();
    }
    
    @Override
    public void bindArray() {
        GL30.glBindVertexArray(pointer);
    }
    
    public void deleteArray() {
        GL30.glDeleteVertexArrays(pointer);
        all.remove(this);
    }
    
    public int arrayId() {
        return pointer;
    }
    
    @Override
    public void addVertexBuffer(VertexBuffer buffer, VertexBufferLayout layout) {
        bindArray();
        buffer.bindBuffer();
        List<VertexBufferElement> elements = layout.getElements();
        int stride = 0;
        int offset = 0;
        for (VertexBufferElement element : elements) {
            stride += element.getCount() * OpenGLUtil.sizeof(element.getType());
        }
        for (int i = 0; i < elements.size(); i++) {
            VertexBufferElement element = elements.get(i);
            GL30.glEnableVertexAttribArray(i);
            GL30.glVertexAttribPointer(i, element.getCount(), OpenGLUtil.typeId(element.getType()),
                    element.isNormalized(), stride, offset);
            offset += element.getCount() * OpenGLUtil.sizeof(element.getType());
        }
    }
    
}
