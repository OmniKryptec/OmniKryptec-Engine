package de.omnikryptec.libapi.opengl.shader;

import org.lwjgl.opengl.GL20;

public class GLUniformImage2D extends GLUniform{

    public GLUniformImage2D(String name) {
        super(name);
        
    }

    public void load(int i) {
        GL20.glUniform1i(getLocation(), i);
    }
}
