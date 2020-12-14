package de.omnikryptec.render3.instancedrect;

import java.util.function.Supplier;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.render3.ArrayFloatCollector;
import de.omnikryptec.render3.BatchCache;
import de.omnikryptec.render3.BatchedRenderer;
import de.omnikryptec.render3.BufferFloatCollector;
import de.omnikryptec.render3.FloatCollector;
import de.omnikryptec.render3.InstanceData;
import de.omnikryptec.render3.InstanceDataProvider;
import de.omnikryptec.render3.InstancedRender;
import de.omnikryptec.render3.instancedrect.InstancedRectBatchCache.CacheEntry;

public abstract class InstancedRectBatchedRenderer<T extends InstancedRectData> implements BatchedRenderer {
    
    private final int floatcollectorSize;
    private final int argSize;
    
    private BufferFloatCollector renderCollector;
    private InstancedRender rendermgr;
    
    private InstancedRectBatchCache batchCache;
    private final boolean caching;
    
    private Texture[] textures;
    private int textureFillIndex = 0;
    
    private FloatCollector currentFloats;
    private int instanceCount = 0;
    
    public InstancedRectBatchedRenderer(boolean cache, int floatcollectorsize, VertexBufferLayout instancedLayout,
            int texAccumSize) {
        this.caching = cache;
        this.floatcollectorSize = floatcollectorsize;
        this.argSize = instancedLayout.getSize();
        this.textures = new Texture[texAccumSize];
        if (!cache) {
            renderCollector = new BufferFloatCollector(floatcollectorsize);
            rendermgr = new InstancedRender(instancedLayout, floatcollectorsize);
        }
    }
    
    protected abstract void fill(FloatCollector target, T id, int textureIndex);
    
    protected abstract void bindShader();
    
    protected void prepareDrawCmd() {
    }
    
    @Override
    public void put(InstanceDataProvider id) {
        if (id == null || id.getInstanceData() == null) {
            return;
        }
        if ((instanceCount + 1) * argSize > currentFloats.remaining()) {
            flushIntern();
        }
        T instanceData = (T) id.getInstanceData();
        int localTextureIndex = setupTexture(instanceData.getTexture());
        fill(currentFloats, instanceData, localTextureIndex);
        instanceCount++;
    }
    
    private int setupTexture(Texture t) {
        Texture base = t == null ? Texture.WHITE_1x1 : t.getBaseTexture();
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
                flushIntern();
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
        flushIntern();
        Texture[] old = this.textures;
        for (CacheEntry ce : irbc.cache) {
            if (ce != null) {
                this.currentFloats.put(ce.collector.getArray(), 0, ce.collector.position());
                this.textures = ce.textures;
                this.instanceCount = ce.instanceCount;
                flushIntern();
            }
        }
        this.textures = old;
    }
    
    @Override
    public void start() {
        if (caching) {
            this.batchCache = new InstancedRectBatchCache();
            this.batchCache.actualClazz = this.getClass();
            this.currentFloats = new ArrayFloatCollector(floatcollectorSize);
        } else {
            bindShader();
            this.currentFloats = renderCollector;
        }
    }
    
    @Override
    public BatchCache flushWithOptionalCache() {
        flushIntern();
        BatchCache returnthis = this.batchCache;
        this.batchCache = null;
        return returnthis;
    }
    
    private void flushIntern() {
        if (instanceCount == 0 || currentFloats.position() == 0) {
            return;
        }
        if (caching) {
            batchCache.push((ArrayFloatCollector) currentFloats, textures.clone(), instanceCount);
            currentFloats = new ArrayFloatCollector(floatcollectorSize);
        } else {
            for (int i = 0; i < textures.length; i++) {
                if (textures[i] == null) {
                    break;
                }
                textures[i].bindTexture(i);
            }
            prepareDrawCmd();
            rendermgr.draw(renderCollector.getBuffer(), instanceCount);
            renderCollector.getBuffer().clear();
        }
        instanceCount = 0;
    }
    
}
