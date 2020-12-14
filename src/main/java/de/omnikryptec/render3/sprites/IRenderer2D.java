package de.omnikryptec.render3.sprites;

import org.joml.Matrix4fc;

public interface IRenderer2D {
    
    void setProjectionViewMatrx(Matrix4fc mat);
    
    void start();
    
    void flush();
}
