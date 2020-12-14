package de.omnikryptec.libapi.opengl.shader;

import org.lwjgl.opengl.GL20;

import de.omnikryptec.libapi.exposed.render.shader.UniformSamplerArray;

public class GLUniformSamplerArray extends GLUniform implements UniformSamplerArray {
    
    private final int size;
    
    public GLUniformSamplerArray(String name, int size) {
        super(name);
        this.size = size;
    }
    
    @Override
    public void setSamplers(int[] units) {
        if (existsInCompilation()) {
            if (units.length != size) {
                throw new IllegalArgumentException("Array size not matching: " + units.length + " vs " + size);
            }
            GL20.glUniform1iv(getLocation(), units);
        }
    }
    
}
