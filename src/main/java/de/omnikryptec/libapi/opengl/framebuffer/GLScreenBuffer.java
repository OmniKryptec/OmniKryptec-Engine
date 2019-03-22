package de.omnikryptec.libapi.opengl.framebuffer;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.util.UnsupportedOperationException;
import de.omnikryptec.util.math.MathUtil;

public class GLScreenBuffer implements SurfaceBuffer {
    private static final FBTarget[] EMPTY = new FBTarget[0];
    private static final int GL_ID = 0;

    private int width;
    private int height;
    //resolveToFrameBuffer needs access
    int[] viewport;
    private double aspectRatio;

    public GLScreenBuffer(long window, double aspectRatio) {
        int[] wA = new int[1];
        int[] hA = new int[1];
        GLFW.glfwGetFramebufferSize(window, wA, hA);
        this.width = wA[0];
        this.height = hA[0];
        this.aspectRatio = aspectRatio;
    }

    @EventSubscription
    public void onBufferSizeChangeInternal(WindowEvent.ScreenBufferResized ev) {
        this.width = ev.width;
        this.height = ev.height;
        setViewport();
    }

    private void setViewport() {
        viewport = MathUtil.calculateViewport(this.aspectRatio, width, height);
        GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    }

    @Override
    public void bindFrameBuffer() {
        if (GLFrameBuffer.history.peek() != this) {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, GL_ID);
            setViewport();
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
    public int[] viewport() {
        return Arrays.copyOf(viewport, viewport.length);
    }

    @Override
    public FBTarget[] targets() {
        //is this correct? somehow create an FBTarget from the display?
        return EMPTY;
    }

    @Override
    public int getWidth() {
        return viewport[2];
    }

    @Override
    public int getHeight() {
        return viewport[3];
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
