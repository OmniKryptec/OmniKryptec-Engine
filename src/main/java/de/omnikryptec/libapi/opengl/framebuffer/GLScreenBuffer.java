package de.omnikryptec.libapi.opengl.framebuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.FrameBufferStack;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.util.UnsupportedOperationException;
import de.omnikryptec.util.math.MathUtil;

public class GLScreenBuffer extends SurfaceBuffer {
    private static final FBTarget[] EMPTY = new FBTarget[0];
    private static final int GL_ID = 0;
    
    private int width;
    private int height;
    //resolveToFrameBuffer needs access
    int[] viewport;
    private final double aspectRatio;
    
    public GLScreenBuffer(final long window, final double aspectRatio, final FrameBufferStack stack) {
        super(stack);
        final int[] wA = new int[1];
        final int[] hA = new int[1];
        GLFW.glfwGetFramebufferSize(window, wA, hA);
        this.width = wA[0];
        this.height = hA[0];
        this.aspectRatio = aspectRatio;
        bindFrameBuffer();
    }
    
    @EventSubscription
    public void onBufferSizeChangeInternal(final WindowEvent.ScreenBufferResized ev) {
        this.width = ev.width;
        this.height = ev.height;
        setViewport();
    }
    
    private void setViewport() {
        this.viewport = MathUtil.calculateViewport(this.aspectRatio, this.width, this.height);
        GL11.glViewport(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);
    }
    
    @Override
    protected void bindRaw() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, GL_ID);
        setViewport();
    }
    
    @Override
    public void clear(final float r, final float g, final float b, final float a, final SurfaceBufferType... types) {
        bindAsTmp();
        OpenGLUtil.setClearColor(r, g, b, a);
        OpenGLUtil.clear(types);
        unbindAsTmp();
    }
    
    @Override
    public int[] getViewportUnsafe() {
        return this.viewport;
    }
    
    @Override
    public FBTarget[] targets() {
        //is this correct? somehow create an FBTarget from the display?
        return EMPTY;
    }
    
    @Override
    public int getWidth() {
        return this.viewport[2];
    }
    
    @Override
    public int getHeight() {
        return this.viewport[3];
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
    public int targetCount() {
        return 1;
    }
    
    @Override
    public FrameBuffer resizedClone(final int newWidth, final int newHeight) {
        throw new UnsupportedOperationException("");
    }
    
    @Override
    public Texture getTexture(final int targetIndex) {
        throw new UnsupportedOperationException("");
    }
    
    @Override
    public void resolveToFrameBuffer(final FrameBuffer target, final int attachment) {
        throw new UnsupportedOperationException("");
    }
    
    @Override
    public void assignTarget(final int index, final FBTarget target) {
        throw new UnsupportedOperationException("");
    }
    
    @Override
    public void deleteRaw() {
        throw new UnsupportedOperationException("");
    }
    
}
