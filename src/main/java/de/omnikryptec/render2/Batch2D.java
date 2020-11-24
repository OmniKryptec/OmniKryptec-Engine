package de.omnikryptec.render2;

import java.util.Objects;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.util.Util;

public class Batch2D {
    
    private BaseRenderer target;
    
    private RenderData2D currentMeta;
    
    public void draw(RenderData2D renderdata) {
        checkDrawing();
        Util.ensureNonNull(renderdata);
        if (this.currentMeta == null) {
            this.target.prepare(renderdata);
        } else if (renderdata.requireUpdateShader() || dif(renderdata)) {
            flush();
            this.target.prepare(renderdata);
        }
        this.currentMeta = renderdata;
        this.target.addData(renderdata);
    }
    
    public void begin(BaseRenderer target) {
        this.target = Util.ensureNonNull(target);
    }
    
    public void end() {
        flush();
        this.target = null;
        this.currentMeta = null;
    }
    
    public void flush() {
        checkDrawing();
        this.target.flush();
    }
    
    public void drawDirect(float[] floats, RenderData2D meta) {
        checkDrawing();
        target.prepare(meta);
        target.addData(floats);
        this.currentMeta = meta;
    }
    
    private boolean dif(RenderData2D meta) {
        if (this.currentMeta.getTextures().length != meta.getTextures().length) {
            return true;
        } else {
            if (meta.getShader() != this.currentMeta.getShader()) {
                return true;
            }
            for (int i = 0; i < meta.getTextures().length; i++) {
                Texture t1 = meta.getTextures()[i];
                Texture t2 = this.currentMeta.getTextures()[i];
                t1 = t1 == null ? null : t1.getBaseTexture();
                t2 = t2 == null ? null : t2.getBaseTexture();
                if (!Objects.equals(t1, t2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void checkDrawing() {
        if (target == null) {
            throw new IllegalStateException("Not drawing");
        }
    }
}
