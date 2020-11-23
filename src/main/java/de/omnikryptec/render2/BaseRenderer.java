package de.omnikryptec.render2;

public interface BaseRenderer {
    
    void prepare(RenderData2D meta);
    
    void addData(float[] floats);
    
    void flush();
}
