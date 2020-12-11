package de.omnikryptec.render3;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

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
import de.omnikryptec.render3.InstancedRectBatchCache.CacheEntry;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.data.DynamicArray;
import de.omnikryptec.resource.MeshData.Primitive;

public class InstancedRectBatchedRenderer implements BatchedRenderer {
    
    private static final int FLOATCOLLECTOR_SIZE = 40000;
    private static final int TEXTURE_ACCUM_SIZE = 8;
    private static final TextureConfig MYCONFIG = new TextureConfig();
    
    private final int instancedArgSize;
    private final Texture NULL_TEXTURE;
    private final RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
    
    private BufferFloatCollector renderCollector;
    private VertexBuffer instanced;
    private VertexArray va;
    private Shader shader;
    
    private InstancedRectBatchCache batchCache;
    private boolean caching = false;
    
    private Texture[] textures = new Texture[TEXTURE_ACCUM_SIZE];
    private int textureFillIndex = 0;
    
    private FloatCollector currentFloats;
    private int instanceCount = 0;
    
    public InstancedRectBatchedRenderer() {
        this.NULL_TEXTURE = LibAPIManager.instance().getGLFW().getRenderAPI()
                .createTexture2D(TextureData.WHITE_TEXTURE_DATA, MYCONFIG);
        renderCollector = new BufferFloatCollector(FLOATCOLLECTOR_SIZE);
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
        instanced.setDescription(BufferUsage.Dynamic, Type.FLOAT, FLOATCOLLECTOR_SIZE);
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
        UniformSamplerArray samplers = new UniformSamplerArray("samplers", TEXTURE_ACCUM_SIZE);
        samplers.loadSamplerArray(IntStream.range(0, TEXTURE_ACCUM_SIZE).toArray());
        shader.bindShader();
        m.loadMatrix(new Matrix4f());
    }
    
    @Override
    public void put(Iterable<? extends Supplier<? extends InstanceData>> list) {
        for (Supplier<? extends InstanceData> id : list) {
            if (id == null || id.get() == null) {
                continue;
            }
            if ((instanceCount + 1) * instancedArgSize > currentFloats.remaining()) {
                flush();
            }
            InstancedRectData instanceData = (InstancedRectData) id.get();
            int localTextureIndex = setupTexture(instanceData.getTexture());
            instanceData.fill(currentFloats);
            currentFloats.put(localTextureIndex);
            instanceCount++;
        }
        
    }
    
    private int setupTexture(Texture t) {
        Texture base = t == null ? NULL_TEXTURE : t.getBaseTexture();
        int index = -1;
        for (int i = 0; i < textures.length; i++) {
            if (textures[i] == null) {
                index = -1;
                break;
            }
            if (textures[i] == base) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            if (textureFillIndex == textures.length) {
                flush();
                textureFillIndex = 0;
                for (int i = 0; i < textures.length; i++) {
                    textures[i] = null;
                }
            }
            textures[textureFillIndex] = base;
            index = textureFillIndex;
            textureFillIndex++;
        }
        return index;
    }
    
    @Override
    public void put(BatchCache cache) {
        InstancedRectBatchCache irbc = (InstancedRectBatchCache) cache;
        flush();
        Texture[] old = this.textures;
        for (CacheEntry ce : irbc.cache) {
            if (ce != null) {
                this.currentFloats.put(ce.collector.getArray(), 0, ce.collector.position());
                this.textures = ce.textures;
                this.instanceCount = ce.instanceCount;
                flush();
            }
        }
        this.textures = old;
    }
    
    @Override
    public void start(Target target) {
        if (caching = target == Target.Cache) {
            this.batchCache = new InstancedRectBatchCache();
            this.currentFloats = new ArrayFloatCollector(FLOATCOLLECTOR_SIZE);
        } else {
            shader.bindShader();
            this.currentFloats = renderCollector;
        }
    }
    
    @Override
    public BatchCache end() {
        flush();
        BatchCache returnthis = this.batchCache;
        this.batchCache = null;
        return returnthis;
    }
    
    private void flush() {
        if (instanceCount == 0 || currentFloats.position() == 0) {
            return;
        }
        if (caching) {
            batchCache.push((ArrayFloatCollector) currentFloats, textures.clone(), instanceCount);
            currentFloats = new ArrayFloatCollector(FLOATCOLLECTOR_SIZE);
        } else {
            for (int i = 0; i < textures.length; i++) {
                if (textures[i] == null) {
                    break;
                }
                textures[i].bindTexture(i);
            }
            instanced.updateData(renderCollector.getBuffer());
            renderCollector.getBuffer().clear();
            api.renderInstanced(va, Primitive.Triangle, 6, instanceCount);
        }
        instanceCount = 0;
    }
    
}
