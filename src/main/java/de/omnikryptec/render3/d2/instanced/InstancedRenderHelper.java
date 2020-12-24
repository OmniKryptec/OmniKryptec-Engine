package de.omnikryptec.render3.d2.instanced;

import java.nio.FloatBuffer;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.resource.MeshData.Primitive;

public class InstancedRenderHelper {
    
    private final RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
    private VertexBuffer instanced;
    private VertexArray va;
    
    public InstancedRenderHelper(VertexBufferLayout instancedLayout, int buffersize) {
        float[] array = new float[] { 0, 0, 0, 1, 1, 0, 1, 1 };
        int[] index = new int[] { 0, 1, 2, 2, 1, 3 };
        IndexBuffer ib = api.createIndexBuffer();
        ib.setDescription(BufferUsage.Static, 6);
        ib.updateData(index);
        VertexBuffer vb = api.createVertexBuffer();
        vb.setDescription(BufferUsage.Static, Type.FLOAT, 8);
        vb.updateData(array);
        va = api.createVertexArray();
        VertexBufferLayout layout = new VertexBufferLayout();
        layout.push(Type.FLOAT, 2, false);
        va.addVertexBuffer(vb, layout);
        instanced = api.createVertexBuffer();
        instanced.setDescription(BufferUsage.Stream, Type.FLOAT, buffersize);
        va.addVertexBuffer(instanced, instancedLayout);
        va.setIndexBuffer(ib);
    }
    
    public void draw(FloatBuffer buffer, int count) {
        instanced.updateData(buffer);
        api.renderInstanced(va, Primitive.Triangle, 6, count);
    }
}
