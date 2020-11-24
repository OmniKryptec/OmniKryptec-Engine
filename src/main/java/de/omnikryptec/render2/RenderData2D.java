package de.omnikryptec.render2;

import de.omnikryptec.libapi.exposed.render.Texture;

public interface RenderData2D extends Cloneable {
    
    float[] getVertexData();
    
    Texture[] getTextures();
    
    default boolean requireUpdateShader() {
        return false;
    }
    
    //Shader specific stuff
    default void updateShader() {
    }
    
    BatchedShader getShader();
    
}
