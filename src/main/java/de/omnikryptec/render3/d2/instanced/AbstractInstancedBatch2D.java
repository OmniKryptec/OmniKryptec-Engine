package de.omnikryptec.render3.d2.instanced;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.render3.d2.BatchCache;
import de.omnikryptec.render3.d2.IBatchedRenderer2D;
import de.omnikryptec.render3.d2.InstanceDataProvider;
import de.omnikryptec.render3.d2.instanced.AbstractInstancedBatchCache.CacheEntry;
import de.omnikryptec.util.data.fc.ArrayFloatCollector;
import de.omnikryptec.util.data.fc.BufferFloatCollector;
import de.omnikryptec.util.data.fc.FloatCollector;

public abstract class AbstractInstancedBatch2D<T extends AbstractInstancedData> implements IBatchedRenderer2D {
    
    private final int floatcollectorSize;
    private final int argSize;
    
    private BufferFloatCollector renderCollector;
    private InstancedRenderHelper rendermgr;
    
    private AbstractInstancedBatchCache batchCache;
    private final boolean caching;
    
    private Texture[] textures;
    private int textureFillIndex = 0;
    
    private FloatCollector currentFloats;
    private int instanceCount = 0;
    
    public AbstractInstancedBatch2D(boolean cache, int floatcollectorsize, VertexBufferLayout instancedLayout,
            int texAccumSize) {
        this.caching = cache;
        this.floatcollectorSize = floatcollectorsize;
        this.argSize = instancedLayout.getSize();
        this.textures = new Texture[texAccumSize];
        if (!cache) {
            renderCollector = new BufferFloatCollector(floatcollectorsize);
            rendermgr = new InstancedRenderHelper(instancedLayout, floatcollectorsize);
        }
    }
    
    protected abstract void fill(FloatCollector target, T id, int textureIndex);
    
    protected abstract void bindShader();
    
    protected void preDrawCmd() {
    }
    
    protected void postDrawCmd() {
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
        AbstractInstancedBatchCache irbc = (AbstractInstancedBatchCache) cache;
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
            this.batchCache = new AbstractInstancedBatchCache();
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
            preDrawCmd();
            rendermgr.draw(renderCollector.getBuffer(), instanceCount);
            postDrawCmd();
            renderCollector.getBuffer().clear();
        }
        instanceCount = 0;
    }
    
}
