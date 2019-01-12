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
    
    private static Deque<GLFrameBuffer> history = new ArrayDeque<>();
    
    private int width;
    private int height;
    
    private FBTexture depthTexture;
    private FBTexture[] textures;
    
    //TODO renderbuffer, multisampling
    
    private final int pointer;
    
    public GLFrameBuffer() {
        pointer = GL30.glGenFramebuffers();
    }
    
    @Override
    public void bindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, pointer);
        history.push(this);
    }
    
    @Override
    public void unbindFrameBuffer() {
        if (history.size() == 1) {
            history.pop();
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        } else if (history.size() > 1) {
            history.pop();
            GLFrameBuffer before = history.pop();
            before.bindFrameBuffer();
        }
    }
    
    @Override
    public Texture getTexture(int i) {
        return textures[i];
    }
    
    @Override
    public Texture getDepthTexture() {
        return depthTexture;
    }
    
    @Override
    protected void deleteRaw() {
        if (depthTexture != null) {
            depthTexture.delete();
        }
        if (textures != null) {
            for (FBTexture t : textures) {
                if (t != null) {
                    t.delete();
                }
            }
        }
        GL30.glDeleteFramebuffers(pointer);
    }
    
    private class FBTexture extends GLTexture {
        
        private FBTexture() {
            super(GL11.GL_TEXTURE_2D);
        }
        
        @Override
        public int getWidth() {
            return width;
        }
        
        @Override
        public int getHeight() {
            return height;
        }
        
    }
    
}
