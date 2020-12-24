package de.omnikryptec.render3.d2.sprites;

import org.joml.Matrix4fc;

public interface IRenderer2D {
    
    void setProjectionViewMatrix(Matrix4fc mat);
    
    void start();
    
    void flush();
}
