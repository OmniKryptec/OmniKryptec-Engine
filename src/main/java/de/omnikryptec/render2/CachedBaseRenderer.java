package de.omnikryptec.render2;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.render.batch.vertexmanager.FloatCollector;

public class CachedBaseRenderer implements BaseRenderer {
    
    private static class PreComputedRenderBatch {
        private float[] floats;
        private RenderData2D meta;
        
        private PreComputedRenderBatch(float[] floats, RenderData2D meta) {
            this.floats = floats;
            this.meta = meta;
        }
    }
    
    private List<PreComputedRenderBatch> cache = new ArrayList<>();
    
    private RenderData2D currentMeta;
    private FloatCollector floats;
    
    public void draw(Batch2D batch) {
        for (PreComputedRenderBatch b : cache) {
            batch.drawDirect(b.floats, b.meta);
        }
    }
    
    public void clear() {
        cache.clear();
    }
    
    @Override
    public void prepare(RenderData2D meta) {
        this.currentMeta = meta;
        if (this.floats == null || this.floats.size() != meta.getShader().getBuffers().getMaxFloats()) {
            this.floats = new FloatCollector(meta.getShader().getBuffers().getMaxFloats());
        }
    }
    
    @Override
    public void addData(float[] floats) {
        if (floats.length > this.floats.remaining()) {
            flush();
        }
        this.floats.put(floats);
    }
    
    @Override
    public void flush() {
        if (floats.used() == 0) {
            return;
        }
        float[] array = new float[floats.used()];
        System.arraycopy(floats.rawArray(), 0, array, 0, array.length);
        cache.add(new PreComputedRenderBatch(array, currentMeta));
        floats.clearArray();
    }
}
