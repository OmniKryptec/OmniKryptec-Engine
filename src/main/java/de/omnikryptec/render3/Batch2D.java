package de.omnikryptec.render3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MutableClassToInstanceMap;

import de.omnikryptec.util.Util;

public class Batch2D {
    
    private ListMultimap<Class<? extends BatchedRenderer>, List<? extends Supplier<? extends InstanceData>>> batch = ArrayListMultimap
            .create();
    private ListMultimap<Class<? extends BatchedRenderer>, InstanceData> indirectBatch = ArrayListMultimap.create();
    private ListMultimap<Class<? extends BatchedRenderer>, BatchCache> cacheBatch = ArrayListMultimap.create();
    private ClassToInstanceMap<BatchedRenderer> rendererImplementations = MutableClassToInstanceMap.create();
    
    private List<BatchCache> cache;
    private boolean drawing;
    
    public <T extends BatchedRenderer> void setInstance(Class<T> clazz, T renderer) {
        Util.ensureNonNull(renderer);
        rendererImplementations.put(clazz, renderer);
    }
    
    public void clearImplementations() {
        rendererImplementations.clear();
    }
    
    public void begin() {
        this.drawing = true;
    }
    
    public void flush() {
        for (Class<? extends BatchedRenderer> r : indirectBatch.keySet()) {
            batch.put(r, indirectBatch.get(r));
        }
        for (Class<? extends BatchedRenderer> rendClass : rendererImplementations.keySet()) {
            //batch.get(r).sort(c); Sort here? Sort in the batched renderer? What? Do we need to sort?
            BatchedRenderer renderer = rendererImplementations.getInstance(rendClass);
            renderer.start();
            for (BatchCache bc : cacheBatch.get(rendClass)) {
                renderer.put(bc);
            }
            for (List<? extends Supplier<? extends InstanceData>> l : batch.get(rendClass)) {//batch.get is not null
                renderer.put(l);
            }
            BatchCache bc = renderer.end();
            if (bc != null) {
                if (cache == null) {
                    cache = new ArrayList<>();
                }
                cache.add(bc);
            }
        }
        indirectBatch.clear();
        batch.clear();
        cacheBatch.clear();
    }
    
    public List<BatchCache> end() {
        flush();
        List<BatchCache> returnthis = this.cache;
        this.cache = null;
        return returnthis;
    }
    
    public void drawCache(BatchCache bc) {
        checkDrawing();
        Util.ensureNonNull(bc);
        cacheBatch.put(bc.getBatchedRendererClass(), bc);
    }
    
    public void drawCache(List<BatchCache> cache) {
        checkDrawing();
        for (BatchCache bc : cache) {
            Util.ensureNonNull(bc);
            cacheBatch.put(bc.getBatchedRendererClass(), bc);
        }
    }
    
    public void drawList(Class<? extends BatchedRenderer> renderer,
            List<? extends Supplier<? extends InstanceData>> d) {
        checkDrawing();
        Util.ensureNonNull(renderer);
        Util.ensureNonNull(d);
        batch.put(renderer, d);
    }
    
    public void draw(Class<? extends BatchedRenderer> renderer, InstanceData data) {
        checkDrawing();
        Util.ensureNonNull(data);
        indirectBatch.put(renderer, data);
    }
    
    private void checkDrawing() {
        if (!drawing) {
            throw new IllegalStateException();
        }
    }
}
