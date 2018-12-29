package de.omnikryptec.libapi.opengl.texture;

import org.lwjgl.opengl.GL11;

import de.omnikryptec.libapi.exposed.AutoDelete;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.opengl.OpenGLUtil;

public abstract class GLTexture extends AutoDelete implements Texture {

    private final int pointer;
    private final int type;

    public GLTexture(final int type) {
        this.pointer = GL11.glGenTextures();
        this.type = type;
    }

    public int textureId() {
        return this.pointer;
    }

    public int textureType() {
        return this.type;
    }

    @Override
    public void bindTexture(final int unit) {
        OpenGLUtil.bindTexture(unit, this.type, this.pointer, false);
    }

    @Override
    protected void deleteRaw() {
        GL11.glDeleteTextures(this.pointer);
    }

}
