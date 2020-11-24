package de.omnikryptec.render2;

import java.nio.FloatBuffer;
import java.util.function.Consumer;

import de.omnikryptec.libapi.exposed.render.Texture;

public interface RenderData2D extends Cloneable {
    
    void fillVertexData(FloatBuffer into);
    
    int vertexDataSize();
    
    Texture[] getTextures();
    
    default boolean requireUpdateShader() {
        return false;
    }
    
    //Shader specific stuff
    default void updateShader() {
    }
    
    BatchedShader getShader();
    
}
