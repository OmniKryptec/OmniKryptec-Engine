package de.omnikryptec.render.batch.vertexmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;

public class OrderedCachedVertexManager implements VertexManager {

    private static class Pair {
        Texture texture;
        float[] poly;
    }

    private final int vertexCount;

    private FloatCollector data;
    private Texture currentTexture;

    private final List<Pair> cache;

    public OrderedCachedVertexManager(final int vertexCount) {
        this.vertexCount = vertexCount;
        this.cache = new ArrayList<>();
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
        final int count = this.data.used();
        final float[] rawArray = this.data.rawArray();
        final float[] newarray = new float[count];
        System.arraycopy(rawArray, 0, newarray, 0, count);
        final Pair p = new Pair();
        p.texture = this.currentTexture;
        p.poly = newarray;
        this.cache.add(p);
    }

    public void draw(final Batch2D batch) {
        for (final Pair p : this.cache) {
            batch.drawPolygon(p.texture, p.poly);
        }
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
