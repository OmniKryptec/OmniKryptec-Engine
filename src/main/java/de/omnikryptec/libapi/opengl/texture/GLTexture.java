package de.omnikryptec.libapi.opengl.texture;

import org.lwjgl.opengl.GL11;

import de.omnikryptec.libapi.exposed.AutoDelete;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.opengl.OpenGLUtil;

public abstract class GLTexture extends AutoDelete implements Texture {
    
    private final int pointer;
    private final int type;
    
    public GLTexture(int type) {
        this.pointer = GL11.glGenTextures();
        this.type = type;
    }
    
    public int textureId() {
        return pointer;
    }
    
    public int textureType() {
        return type;
    }
    
    @Override
    public void bindTexture(int unit) {
        OpenGLUtil.bindTexture(unit, type, pointer, false);
    }
    
    @Override
    protected void deleteRaw() {
        GL11.glDeleteTextures(pointer);
    }
    
}
