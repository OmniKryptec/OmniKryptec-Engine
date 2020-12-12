package de.omnikryptec.render3.instancedrect;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.ArrayFloatCollector;
import de.omnikryptec.render3.BatchCache;
import de.omnikryptec.render3.BatchedRenderer;
import de.omnikryptec.util.data.DynamicArray;

public class InstancedRectBatchCache implements BatchCache {
    
    static class CacheEntry {
        ArrayFloatCollector collector;
        Texture[] textures;
        int instanceCount;
    }
    
    DynamicArray<CacheEntry> cache = new DynamicArray<>();
    
    void push(ArrayFloatCollector buffer, Texture[] textures, int instancecount) {
        CacheEntry e = new CacheEntry();
        e.collector = buffer;
        e.textures = textures;
        e.instanceCount = instancecount;
        cache.add(e);
    }
    
    @Override
    public Class<? extends BatchedRenderer> getBatchedRendererClass() {
        return InstancedRectBatchedRenderer.class;
    }
}
