package de.omnikryptec.render3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.omnikryptec.util.Util;

public class Batch2D {
    
    public static enum Target {
        Render, Cache
    }
    
    private Target currentTarget;
    private ListMultimap<BatchedRenderer, List<? extends Supplier<? extends InstanceData>>> batch = ArrayListMultimap
            .create();
    private ListMultimap<BatchedRenderer, InstanceData> indirectBatch = ArrayListMultimap.create();
    private ListMultimap<BatchedRenderer, BatchCache> cacheBatch = ArrayListMultimap.create();
    private Set<BatchedRenderer> renderers = new HashSet<>();
    
    private List<BatchCache> cache;
    
    public void begin(Target target) {
        if (currentTarget != null) {
            throw new IllegalStateException();
        }
        this.currentTarget = Util.ensureNonNull(target);
        if (this.currentTarget == Target.Cache) {
            this.cache = new ArrayList<>();
        }
    }
    
    public void flush() {
        for (BatchedRenderer r : indirectBatch.keySet()) {
            batch.put(r, indirectBatch.get(r));
        }
        for (BatchedRenderer r : renderers) {
            //batch.get(r).sort(c); TODO Sort here? Sort in the batched renderer? What? O
            r.start(currentTarget);
            for (BatchCache bc : cacheBatch.get(r)) {
                r.put(bc);
            }
            for (List<? extends Supplier<? extends InstanceData>> l : batch.get(r)) {//batch.get is not null
                r.put(l);
            }
            BatchCache bc = r.end();
            if (currentTarget == Target.Cache) {
                Util.ensureNonNull(bc);
                cache.add(bc);
            }
        }
        indirectBatch.clear();
        batch.clear();
        cacheBatch.clear();
        renderers.clear();//is this even needed?
    }
    
    public List<BatchCache> end() {
        flush();
        List<BatchCache> returnthis = this.cache;
        this.cache = null;
        this.currentTarget = null;
        return returnthis;
    }
    
    public void drawCache(List<BatchCache> cache) {
        checkDrawing();
        for (BatchCache bc : cache) {
            Util.ensureNonNull(bc);
            cacheBatch.put(bc.getBatchedRenderer(), bc);
            renderers.add(bc.getBatchedRenderer());
        }
    }
    
    public void drawList(BatchedRenderer renderer, List<? extends Supplier<? extends InstanceData>> d) {
        checkDrawing();
        Util.ensureNonNull(renderer);
        Util.ensureNonNull(d);
        batch.put(renderer, d);
        renderers.add(renderer);
    }
    
    public void draw(InstanceData data) {
        checkDrawing();
        Util.ensureNonNull(data);
        indirectBatch.put(data.getBatchedRenderer(), data);
        renderers.add(data.getBatchedRenderer());
    }
    
    private void checkDrawing() {
        if (currentTarget == null) {
            throw new IllegalStateException();
        }
    }
}
