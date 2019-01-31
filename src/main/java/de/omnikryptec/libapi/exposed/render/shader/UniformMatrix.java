package de.omnikryptec.libapi.exposed.render.shader;

import org.joml.Matrix4fc;

public interface UniformMatrix extends Uniform {
    
    void loadMatrix(Matrix4fc mat);
    
}
