/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.libapi.opengl.framebuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import de.omnikryptec.event.Event;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
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
    
    public static class ScreenBufferResizedEvent extends Event {
        public final SurfaceBuffer surfaceBuffer;
        public final int width;
        public final int height;
        
        private ScreenBufferResizedEvent(SurfaceBuffer b, int w, int h) {
            this.surfaceBuffer = b;
            this.width = w;
            this.height = h;
        }
    }
    
    private int nativeWidth;
    private int nativeHeight;
    //resolveToFrameBuffer needs access
    int[] viewport;
    private final double aspectRatio;
    
    public GLScreenBuffer(final long window, final double aspectRatio, final FrameBufferStack stack) {
        super(stack);
        final int[] wA = new int[1];
        final int[] hA = new int[1];
        GLFW.glfwGetFramebufferSize(window, wA, hA);
        this.nativeWidth = wA[0];
        this.nativeHeight = hA[0];
        this.aspectRatio = aspectRatio;
        bindFrameBuffer();//<- also sets the Viewport (through bindRaw())
    }
    
    @EventSubscription
    public void onBufferSizeChangeInternal(final WindowEvent.ScreenBufferResizedNative ev) {
        this.nativeWidth = ev.width;
        this.nativeHeight = ev.height;
        setViewport();
        LibAPIManager.ENGINE_EVENTBUS.post(new ScreenBufferResizedEvent(this, getWidth(), getHeight()));
    }
    
    private void setViewport() {
        this.viewport = MathUtil.calculateViewport(this.aspectRatio, this.nativeWidth, this.nativeHeight);
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
