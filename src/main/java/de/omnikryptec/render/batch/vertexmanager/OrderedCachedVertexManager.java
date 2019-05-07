package de.omnikryptec.render.batch.vertexmanager;

import java.util.ArrayList;
import java.util.Comparator;
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
    
    private List<Pair> cache;
    
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
        Texture baseTexture = texture == null ? null : texture.getBaseTexture();
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
        float[] rawArray = data.rawArray();
        float[] newarray = new float[count];
        System.arraycopy(rawArray, 0, newarray, 0, count);
        Pair p = new Pair();
        p.texture = currentTexture;
        p.poly = newarray;
        cache.add(p);
    }
    
    public void draw(Batch2D batch) {
        for (Pair p : cache) {
            batch.drawPolygon(p.texture, p.poly);
        }
    }
    
    public void clear() {
        cache.clear();
    }
    
    @Override
    public void init(ModuleBatchingManager mgr) {
        this.data = new FloatCollector(vertexCount * mgr.floatsPerVertex());
    }
    
    @Override
    public void begin() {
    }
}
