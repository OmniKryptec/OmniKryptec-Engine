package de.omnikryptec.render2;

public interface BatchedShader {
    
    RenderData2D createRenderData();
    
    BufferHolder getBuffers();
}
