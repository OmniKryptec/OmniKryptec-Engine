package de.omnikryptec.render3.d2.instanced;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.d2.BatchCache;
import de.omnikryptec.render3.d2.IBatchedRenderer2D;
import de.omnikryptec.util.data.DynamicArray;
import de.omnikryptec.util.data.fc.ArrayFloatCollector;

public class AbstractInstancedBatchCache implements BatchCache {
    
    static class CacheEntry {
        ArrayFloatCollector collector;
        Texture[] textures;
        int instanceCount;
    }
    
    DynamicArray<CacheEntry> cache = new DynamicArray<>();
    
    Class<? extends IBatchedRenderer2D> actualClazz;
    
    void push(ArrayFloatCollector buffer, Texture[] textures, int instancecount) {
        CacheEntry e = new CacheEntry();
        e.collector = buffer;
        e.textures = textures;
        e.instanceCount = instancecount;
        cache.add(e);
    }
    
    @Override
    public Class<? extends IBatchedRenderer2D> getBatchedRendererClass() {
        return actualClazz;
    }
}
