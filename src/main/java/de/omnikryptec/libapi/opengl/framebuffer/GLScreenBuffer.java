package de.omnikryptec.libapi.opengl.framebuffer;

import org.lwjgl.opengl.GL30;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.util.UnsupportedOperationException;

public class GLScreenBuffer implements FrameBuffer {
    private static final FBTarget[] EMPTY = new FBTarget[0];
    private static final int GL_ID = 0;
    
    public static final GLScreenBuffer TMP = new GLScreenBuffer();
    
    private int width;
    private int height;
    private int[] viewport;
    
    @Override
    public void bindFrameBuffer() {
        if (GLFrameBuffer.history.peek() != this) {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, GL_ID);
            //TODO set viewport
            RenderAPI.get().getWindow().refreshViewport();
            GLFrameBuffer.history.push(this);
        }
    }
    
    @Override
    public void unbindFrameBuffer() {
        if (GLFrameBuffer.history.peek() == this) {
            if (GLFrameBuffer.history.size() > 1) {
                GLFrameBuffer.history.pop();
                GLFrameBuffer.history.pop().bindFrameBuffer();
            }
        } else {
            throw new IllegalStateException("can not unbind if not top of framebuffer stack!");
        }
    }
    
    @Override
    public FBTarget[] targets() {
        //is this correct? somehow create an FBTarget from the display?
        return EMPTY;
    }
    
    //FIXME remove tmp things
    @Override
    public int getWidth() {
        return RenderAPI.get().getWindow().getBufferWidth();
    }
    
    @Override
    public int getHeight() {
        return RenderAPI.get().getWindow().getBufferHeight();
    }
    
    @Override
    public int multisamples() {
        return 0;
    }
    
    @Override
    public boolean isRenderBuffer() {
        return false;
    }
    
    @Override
    public int size() {
        return 1;
    }
    
    @Override
    public FrameBuffer resizedClone(int newWidth, int newHeight) {
        throw new UnsupportedOperationException("");
    }
    
    @Override
    public Texture getTexture(int targetIndex) {
        throw new UnsupportedOperationException("");
    }
    
    @Override
    public void resolveToFrameBuffer(FrameBuffer target, int attachment) {
        throw new UnsupportedOperationException("");
    }
    
    @Override
    public void assignTarget(int index, FBTarget target) {
        throw new UnsupportedOperationException("");
    }
    
}
