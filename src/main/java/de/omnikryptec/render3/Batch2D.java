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
    
    private ArrayListMultimap<Class<? extends IBatchedRenderer2D>, List<? extends InstanceDataProvider>> batch = ArrayListMultimap
            .create();
    private ListMultimap<Class<? extends IBatchedRenderer2D>, InstanceDataProvider> indirectBatch = ArrayListMultimap.create();
    private ListMultimap<Class<? extends IBatchedRenderer2D>, BatchCache> cacheBatch = ArrayListMultimap.create();
    private ClassToInstanceMap<IBatchedRenderer2D> rendererImplementations = MutableClassToInstanceMap.create();
    
    private boolean autoclear = true;
    private boolean updateIndirect;
    
    public void setInstance(IBatchedRenderer2D renderer) {
        this.setInstance(renderer.getClass(), renderer);
    }
    
    public void setInstance(Class<? extends IBatchedRenderer2D> clazz, IBatchedRenderer2D renderer) {
        Util.ensureNonNull(renderer);
        rendererImplementations.put(clazz, renderer);
    }
    
    public void clearImplementations() {
        rendererImplementations.clear();
    }
    
    public List<BatchCache> flush() {
        List<BatchCache> cache = null;
        if (updateIndirect) {
            updateIndirect = false;
            for (Class<? extends IBatchedRenderer2D> r : indirectBatch.keySet()) {
                List<? extends InstanceDataProvider> list = indirectBatch.get(r);
                if (!batch.containsEntry(r, list)) {
                    batch.put(r, list);
                }
            }
        }
        for (Class<? extends IBatchedRenderer2D> rendClass : rendererImplementations.keySet()) {
            //batch.get(r).sort(c); Sort here? Sort in the batched renderer? What? Do we need to sort?
            IBatchedRenderer2D renderer = rendererImplementations.getInstance(rendClass);
            for (BatchCache bc : cacheBatch.get(rendClass)) {
                renderer.put(bc);
            }
            for (List<? extends InstanceDataProvider> l : batch.get(rendClass)) {//batch.get is not null
                for (InstanceDataProvider idp : l) {
                    renderer.put(idp);
                }
            }
            BatchCache bc = renderer.flushWithOptionalCache();
            if (bc != null) {
                if (cache == null) {
                    cache = new ArrayList<>();
                }
                cache.add(bc);
            }
        }
        if (autoclear) {
            clearData();
        }
        return cache;
    }
    
    public boolean isAutoclear() {
        return autoclear;
    }
    
    public void setAutoclear(boolean autoclear) {
        this.autoclear = autoclear;
    }
    
    public void clearData() {
        indirectBatch.clear();
        batch.clear();
        cacheBatch.clear();
    }
    
    public void drawCache(BatchCache bc) {
        Util.ensureNonNull(bc);
        cacheBatch.put(bc.getBatchedRendererClass(), bc);
    }
    
    public void drawCache(List<BatchCache> cache) {
        for (BatchCache bc : cache) {
            Util.ensureNonNull(bc);
            cacheBatch.put(bc.getBatchedRendererClass(), bc);
        }
    }
    
    public void drawList(Class<? extends IBatchedRenderer2D> renderer, List<? extends InstanceDataProvider> d) {
        Util.ensureNonNull(renderer);
        Util.ensureNonNull(d);
        batch.put(renderer, d);
    }
    
    public void draw(InstanceDataProvider data) {
        draw(Util.ensureNonNull(data.getInstanceData().getDefaultRenderer()), data);
    }
    
    public void draw(Class<? extends IBatchedRenderer2D> renderer, InstanceDataProvider data) {
        Util.ensureNonNull(data);
        indirectBatch.put(renderer, data);
        updateIndirect = true;
    }
    
    public void remove(InstanceDataProvider data) {
        this.remove(data.getInstanceData().getDefaultRenderer(), data);
    }
    
    public void remove(Class<? extends IBatchedRenderer2D> renderer, InstanceDataProvider data) {
        indirectBatch.remove(renderer, data);
        updateIndirect = true;
    }
    
    public void removeList(Class<? extends IBatchedRenderer2D> renderer,
            List<? extends InstanceDataProvider> d) {
        batch.remove(renderer, d);
    }
    
    public void removeCache(List<BatchCache> caches) {
        for (BatchCache bc : caches) {
            cacheBatch.remove(bc.getBatchedRendererClass(), bc);
        }
    }
    
    public void removeCache(BatchCache bc) {
        cacheBatch.remove(bc.getBatchedRendererClass(), bc);
    }
}
