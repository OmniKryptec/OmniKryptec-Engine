package de.omnikryptec.render3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.NotImplementedException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.data.IterableCombiner;

public class Batch2D {
    
    public static enum Target {
        Render, Cache
    }
    
    private Target currentTarget;
    private ListMultimap<BatchedRenderer, List<? extends Supplier<InstanceData>>> batch = ArrayListMultimap.create();
    private ListMultimap<BatchedRenderer, InstanceData> indirectBatch = ArrayListMultimap.create();
    
    public void begin(Target target) {
        if (currentTarget != null) {
            throw new IllegalStateException();
        }
        this.currentTarget = Util.ensureNonNull(target);
    }
    
    public void flush() {
        Collection<BatchedRenderer> renderers = batch.keySet();
        for (BatchedRenderer r : indirectBatch.keySet()) {
            batch.put(r, indirectBatch.get(r));
        }
        for (BatchedRenderer r : renderers) {
            //batch.get(r).sort(c); TODO Sort here? Sort in the batched renderer? What? O
            switch (currentTarget) {
            case Cache:
                throw new NotImplementedException("Cache is not yet implemented");
            case Render:
                r.render(new IterableCombiner<>(batch.get(r).toArray(Iterable[]::new)));
                break;
            default:
                throw new IllegalStateException();
            }
        }
        indirectBatch.clear();
        batch.clear();
    }
    
    public void end() {
        flush();
        this.currentTarget = null;
    }
    
    public void drawList(List<? extends Supplier<InstanceData>> d) {
        checkDrawing();
        BatchedRenderer r = d.get(0).get().getBatchedRenderer();//a little bit nasty
        batch.put(r, d);
    }
    
    public void draw(InstanceData data) {
        checkDrawing();
        Util.ensureNonNull(data);
        indirectBatch.put(data.getBatchedRenderer(), data);
    }
    
    private void checkDrawing() {
        if (currentTarget == null) {
            throw new IllegalStateException();
        }
    }
}
