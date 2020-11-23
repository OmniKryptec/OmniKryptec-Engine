package de.omnikryptec.render2;

import de.omnikryptec.libapi.exposed.render.Texture;

public interface RenderData2D extends Cloneable {
    
    float[] getVertexData();
    
    Texture[] getTextures();
    
    boolean requireUpdateShader();
    
    //Shader specific stuff
    void updateShader();
    
    BatchedShader getShader();
    
}
