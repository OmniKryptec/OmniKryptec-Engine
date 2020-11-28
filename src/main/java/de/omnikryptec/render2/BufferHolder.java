package de.omnikryptec.render2;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.resource.MeshData.Primitive;

public class BufferHolder {
    
    private VertexArray va;
    private VertexBuffer vb;
    private FloatBuffer buffer;
    private int floatsPerVertex;
    private int maxfloats;
    
    public BufferHolder(VertexBufferLayout layout, int vertexCount) {
        this.floatsPerVertex = layout.getSize();
        this.maxfloats = vertexCount * this.floatsPerVertex;
        this.buffer = BufferUtils.createFloatBuffer(this.maxfloats);
        this.vb = LibAPIManager.instance().getGLFW().getRenderAPI().createVertexBuffer();
        this.vb.setDescription(BufferUsage.Dynamic, Type.FLOAT, vertexCount * this.floatsPerVertex);
        this.va = LibAPIManager.instance().getGLFW().getRenderAPI().createVertexArray();
        this.va.addVertexBuffer(this.vb, layout);
    }
    
    
   
    
    
    public void flush() {
        final int count = this.buffer.position();
        if (count == 0) {
            return;
        }
        this.vb.updateData(this.buffer);
        this.buffer.clear();
        LibAPIManager.instance().getGLFW().getRenderAPI().render(this.va, Primitive.Triangle,
                count / this.floatsPerVertex);
    }
    
    public int getMaxFloats() {
        return this.maxfloats;
    }
    
    //but note that here only complete data sets should be put into, otherwise... oof.
    public void addData(final float[] floats, final int offset, final int length) {
        if (length > buffer.remaining()) {
            flush();//Nobody has to no this ever happened... :) 
        }
        this.buffer.put(floats, offset, length);
    }
    
    public void addData(RenderData2D dd) {
        if (dd.vertexDataSize() > buffer.remaining()) {
            flush();
        }
        dd.fillVertexData(buffer);
    }
    
}
