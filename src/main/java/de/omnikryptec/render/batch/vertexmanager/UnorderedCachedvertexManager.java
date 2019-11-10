package de.omnikryptec.render.batch.vertexmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;
import de.omnikryptec.render.renderer.RendererUtil;

public class UnorderedCachedvertexManager implements VertexManager {
    private final int vertexCount;

    private FloatCollector data;
    private Texture currentTexture;

    private final Map<Texture, float[]> cache;

    public UnorderedCachedvertexManager(final int vertexCount) {
        this.vertexCount = vertexCount;
        this.cache = new HashMap<>();
    }

    @Override
    public void addData(final float[] floats, final int offset, final int length) {
        this.data.put(floats, offset, length);
    }

    @Override
    public void prepareNext(final Texture texture, final int requiredFloats) {
        if (requiredFloats > this.data.size()) {
            throw new IndexOutOfBoundsException("Can't handle mesh, buffer too small");
        }
        final Texture baseTexture = texture == null ? null : texture.getBaseTexture();
        if (requiredFloats > this.data.remaining() || !Objects.equals(baseTexture, this.currentTexture)) {
            //flush BEFORE setting new texture
            forceFlush();
            this.currentTexture = baseTexture;
        }
    }

    @Override
    public void forceFlush() {
        if (this.data.used() == 0) {
            return;
        }
        //transfer used data from the collector into the map-entry, dont override existing values
        final int count = this.data.used();
        final float[] newfloats = this.data.rawArray();
        final float[] old = this.cache.get(this.currentTexture);
        final int oldlen = old == null ? 0 : old.length;
        final float[] newarray = new float[oldlen + count];
        if (oldlen > 0) {
            System.arraycopy(old, 0, newarray, 0, oldlen);
        }
        System.arraycopy(newfloats, 0, newarray, oldlen, count);
        this.cache.put(this.currentTexture, newarray);
    }

    public void draw(final Batch2D batch) {
        RendererUtil.drawUnorderedCache(batch, this.cache);
    }

    public Map<Texture, float[]> getCache() {
        return this.cache;
    }

    public void clear() {
        this.cache.clear();
    }

    @Override
    public void init(final ModuleBatchingManager mgr) {
        this.data = new FloatCollector(this.vertexCount * mgr.floatsPerVertex());
    }

    @Override
    public void begin() {
    }
}
