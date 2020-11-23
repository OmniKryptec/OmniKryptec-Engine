package de.omnikryptec.render2;

public class Batch2D {
    
    private BaseRenderer target;
    
    public void draw(RenderData2D renderdata) {
        if (renderdata.requireUpdateShader()) {
            flush();//TODO texture changes etc
        }
        this.target.prepare(renderdata);
        this.target.addData(renderdata.getVertexData());
    }
    
    public void drawDirect(float[] floats, RenderData2D meta) {
        checkDrawing();
        target.prepare(meta);
        target.addData(floats);
    }
    
    public void begin(BaseRenderer target) {
        this.target = target;
    }
    
    public void end() {
        flush();
        this.target = null;
    }
    
    public void flush() {
        checkDrawing();
        this.target.flush();
    }
    
    private void checkDrawing() {
        if (target == null) {
            throw new IllegalStateException("Not drawing");
        }
    }
}
