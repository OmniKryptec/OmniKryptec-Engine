package de.omnikryptec.render3;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.resource.MeshData.Primitive;

public class InstancedRectBatchedRenderer implements BatchedRenderer {
    
    private static final int bsize = 10000;
    
    private FloatBuffer buffer;
    private VertexBuffer instanced;
    private RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
    private VertexArray va;
    private Shader shader;
    
    public InstancedRectBatchedRenderer() {
        buffer = BufferUtils.createFloatBuffer(bsize);
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
        //layout.push(Type.FLOAT, 2, false);
        va.addVertexBuffer(vb, layout);
        instanced = api.createVertexBuffer();
        instanced.setDescription(BufferUsage.Dynamic, Type.FLOAT, bsize);
        VertexBufferLayout lay2 = new VertexBufferLayout();
        lay2.push(Type.FLOAT, 2, false, 1);
        lay2.push(Type.FLOAT, 2, false, 1);
        lay2.push(Type.FLOAT, 2, false, 1);
        va.addVertexBuffer(instanced, lay2);
        va.setIndexBuffer(ib);
        shader = api.createShader();
        shader.create("gurke");
        UniformMatrix m = shader.getUniform("u_projview");
        shader.bindShader();
        m.loadMatrix(new Matrix4f());
    }
    
    @Override
    public void render(List<InstanceData> list) {
        int count = 0;
        for (InstanceData id : list) {
            InstancedRectData d = (InstancedRectData) id;
            if (count * 6 + 6 > buffer.remaining()) {
                flush(count);
                count = 0;
            }
            d.fill(buffer);
            count++;
        }
        if (count != 0) {
            flush(count);
            count = 0;
        }
    }
    
    private void flush(int count) {
        if (buffer.position() == 0) {
            return;
        }
        instanced.updateData(buffer);
        buffer.clear();
        shader.bindShader();
        api.renderInstanced(va, Primitive.Triangle, 6, count);
    }
    
    @Override
    public void render(BatchCache cache) {
    }
    
    @Override
    public BatchCache prepare(List<InstanceData> list) {
        return null;
    }
    
}
