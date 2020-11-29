package de.omnikryptec.render3;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.NotImplementedException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.omnikryptec.util.Util;

public class Batch2D {
    
    public static enum Target {
        Render, Cache
    }
    
    private Target currentTarget;
    private ListMultimap<BatchedRenderer, InstanceData> batch = ArrayListMultimap.create();
    
    public void begin(Target target) {
        if (currentTarget != null) {
            throw new IllegalStateException();
        }
        this.currentTarget = Util.ensureNonNull(target);
    }
    
    public void flush() {
        Collection<BatchedRenderer> renderers = batch.keySet();
        for (BatchedRenderer r : renderers) {
            //batch.get(r).sort(c); TODO Sort here? Sort in the batched renderer? What? Oof
            switch (currentTarget) {
            case Cache:
                throw new NotImplementedException("Cache is not yet implemented");
            case Render:
                r.render(batch.get(r));
                break;
            default:
                throw new IllegalStateException();
            }
        }
        batch.clear();
    }
    
    public void end() {
        flush();
        this.currentTarget = null;
    }
    
    public void drawDirect(List<? extends Supplier<InstanceData>> d) {//this is WIP or something
        BatchedRenderer r = d.get(0).get().getBatchedRenderer();
        r.render(d);
    }
    
    public void draw(InstanceData data) {
        checkDrawing();
        Util.ensureNonNull(data);
        batch.put(data.getBatchedRenderer(), data);
    }
    
    private void checkDrawing() {
        if (currentTarget == null) {
            throw new IllegalStateException();
        }
    }
}
