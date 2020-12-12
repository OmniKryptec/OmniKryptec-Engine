package de.omnikryptec.render3.instancedrect;

import java.util.function.Supplier;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.ArrayFloatCollector;
import de.omnikryptec.render3.BatchCache;
import de.omnikryptec.render3.BatchedRenderer;
import de.omnikryptec.render3.BufferFloatCollector;
import de.omnikryptec.render3.FloatCollector;
import de.omnikryptec.render3.InstanceData;
import de.omnikryptec.render3.InstancedRender;
import de.omnikryptec.render3.Batch2D.Target;
import de.omnikryptec.render3.instancedrect.InstancedRectBatchCache.CacheEntry;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;

public class InstancedRectBatchedRenderer implements BatchedRenderer {
    
    private static final int FLOATCOLLECTOR_SIZE = 40000;
    static final int TEXTURE_ACCUM_SIZE = 8;
    private static final TextureConfig MYCONFIG = new TextureConfig();
    
    private final int instancedArgSize;
    private final Texture NULL_TEXTURE;
    
    private BufferFloatCollector renderCollector;
    private InstancedRender rendermgr;
    private InstancedRectShader shader;
    
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
        VertexBufferLayout instancedLayout = new VertexBufferLayout();
        instancedLayout.push(Type.FLOAT, 2, false, 1);
        instancedLayout.push(Type.FLOAT, 2, false, 1);
        instancedLayout.push(Type.FLOAT, 2, false, 1);
        instancedLayout.push(Type.FLOAT, 4, false, 1);
        instancedLayout.push(Type.FLOAT, 4, false, 1);
        instancedLayout.push(Type.FLOAT, 1, false, 1);
        instancedArgSize = instancedLayout.getSize();
        rendermgr = new InstancedRender(instancedLayout, FLOATCOLLECTOR_SIZE);
        shader = new InstancedRectShader();
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
            rendermgr.draw(renderCollector.getBuffer(), instanceCount);
            renderCollector.getBuffer().clear();
        }
        instanceCount = 0;
    }
    
}
