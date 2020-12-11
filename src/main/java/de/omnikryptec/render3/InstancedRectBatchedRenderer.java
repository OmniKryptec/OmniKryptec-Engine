package de.omnikryptec.render3;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.function.Supplier;

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
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrixArray;
import de.omnikryptec.libapi.exposed.render.shader.UniformSamplerArray;
import de.omnikryptec.render3.Batch2D.Target;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.data.DynamicArray;
import de.omnikryptec.resource.MeshData.Primitive;

public class InstancedRectBatchedRenderer implements BatchedRenderer {
    
    private static final int bsize = 40000;
    
    private static final int texcount = 8;
    
    private FloatBuffer buffer;
    private VertexBuffer instanced;
    private RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
    private VertexArray va;
    private Shader shader;
    
    private int instancedArgSize;
    
    private final Texture NULL_TEXTURE;
    private static final TextureConfig MYCONFIG = new TextureConfig();
    
    private Texture[] textures = new Texture[texcount];
    private int textureFillIndex = 0;
    
    public InstancedRectBatchedRenderer() {
        this.NULL_TEXTURE = LibAPIManager.instance().getGLFW().getRenderAPI()
                .createTexture2D(TextureData.WHITE_TEXTURE_DATA, MYCONFIG);
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
        va.addVertexBuffer(vb, layout);
        instanced = api.createVertexBuffer();
        instanced.setDescription(BufferUsage.Dynamic, Type.FLOAT, bsize);
        VertexBufferLayout lay2 = new VertexBufferLayout();
        lay2.push(Type.FLOAT, 2, false, 1);
        lay2.push(Type.FLOAT, 2, false, 1);
        lay2.push(Type.FLOAT, 2, false, 1);
        lay2.push(Type.FLOAT, 4, false, 1);
        lay2.push(Type.FLOAT, 4, false, 1);
        lay2.push(Type.FLOAT, 1, false, 1);
        instancedArgSize = lay2.getSize();
        va.addVertexBuffer(instanced, lay2);
        va.setIndexBuffer(ib);
        shader = api.createShader();
        shader.create("gurke");
        UniformMatrix m = shader.getUniform("u_projview");
        UniformSamplerArray samplers = new UniformSamplerArray("samplers", 8);
        samplers.loadMatrixArray(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 });
        shader.bindShader();
        m.loadMatrix(new Matrix4f());
    }
    
    private int instanceCount = 0;
    
    @Override
    public void put(Iterable<? extends Supplier<? extends InstanceData>> list) {
        shader.bindShader();
        for (Supplier<? extends InstanceData> id : list) {
            if (id == null || id.get() == null) {
                continue;
            }
            if ((instanceCount + 1) * instancedArgSize > buffer.remaining()) {
                flush(instanceCount);
                instanceCount = 0;
            }
            InstancedRectData instanceData = (InstancedRectData) id.get();
            int localTextureIndex = findTexture(instanceData.getTexture());
            if (localTextureIndex == -1) {
                textureFillIndex++;
                if (textureFillIndex == textures.length) {
                    flush(instanceCount);
                    instanceCount = 0;
                    textureFillIndex = 0;
                }
                textures[textureFillIndex] = getBaseTexture(instanceData.getTexture());
                localTextureIndex = textureFillIndex;
            }
            instanceData.fill(buffer);
            buffer.put(localTextureIndex);
            instanceCount++;
        }
        
    }
    
    @Override
    public void start(Target target) {
    }
    
    @Override
    public BatchCache end() {
        if (instanceCount != 0) {
            flush(instanceCount);
            instanceCount = 0;
        }
        return null;
    }
    
    private int findTexture(Texture t) {
        Texture base = getBaseTexture(t);
        for (int i = 0; i < textures.length; i++) {
            if (textures[i] == null) {
                return -1;
            }
            if (textures[i] == base) {
                return i;
            }
        }
        return -1;
    }
    
    private Texture getBaseTexture(Texture t) {
        if (t == null) {
            return NULL_TEXTURE;
        }
        return t.getBaseTexture();
    }
    
    private void flush(int count) {
        if (count == 0 || buffer.position() == 0) {
            return;
        }
        for (int i = 0; i < textures.length; i++) {
            if (textures[i] == null) {
                break;
            }
            textures[i].bindTexture(i);
        }
        instanced.updateData(buffer);
        buffer.clear();
        api.renderInstanced(va, Primitive.Triangle, 6, count);
    }
    
    @Override
    public void put(BatchCache cache) {
    }
    
}
