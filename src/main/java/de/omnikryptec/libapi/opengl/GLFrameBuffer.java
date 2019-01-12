package de.omnikryptec.libapi.opengl;

import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import de.omnikryptec.libapi.exposed.AutoDelete;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.opengl.texture.GLTexture;

public class GLFrameBuffer extends AutoDelete implements FrameBuffer {
    
    private static final Deque<GLFrameBuffer> history = new ArrayDeque<>();
    
    private int width;
    private int height;
    
    private FBTexture depthTexture;
    private FBTexture[] textures;
    
    //TODO renderbuffer, multisampling
    
    private final int pointer;
    
    public GLFrameBuffer() {
        this.pointer = GL30.glGenFramebuffers();
    }
    
    @Override
    public void bindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.pointer);
        history.push(this);
    }
    
    @Override
    public void unbindFrameBuffer() {
        if (history.peek() == this) {
            if (history.size() == 1) {
                history.pop();
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
            } else if (history.size() > 1) {
                history.pop();
                final GLFrameBuffer before = history.pop();
                before.bindFrameBuffer();
            }
        }
    }
    
    @Override
    public Texture getTexture(final int i) {
        return this.textures[i];
    }
    
    @Override
    public Texture getDepthTexture() {
        return this.depthTexture;
    }
    
    @Override
    protected void deleteRaw() {
        if (this.depthTexture != null) {
            this.depthTexture.delete();
        }
        if (this.textures != null) {
            for (final FBTexture t : this.textures) {
                if (t != null) {
                    t.delete();
                }
            }
        }
        GL30.glDeleteFramebuffers(this.pointer);
    }
    
    private class FBTexture extends GLTexture {
        
        private FBTexture() {
            super(GL11.GL_TEXTURE_2D);
        }
        
        @Override
        public int getWidth() {
            return GLFrameBuffer.this.width;
        }
        
        @Override
        public int getHeight() {
            return GLFrameBuffer.this.height;
        }
        
    }
    
}
