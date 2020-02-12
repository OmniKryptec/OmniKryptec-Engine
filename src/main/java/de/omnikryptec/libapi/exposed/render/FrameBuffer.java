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

package de.omnikryptec.libapi.exposed.render;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.render.renderer.RendererUtil;
import de.omnikryptec.util.data.Color;

/**
 * An abstract class representing a FrameBuffer.<br>
 * FrameBuffers are dynamic textures, e.g. used for postprocessing.
 *
 * @author pcfreak9000
 * @see RenderAPI#createFrameBuffer(int, int, int, int)
 */
public abstract class FrameBuffer implements Deletable {
    
    protected final FrameBufferStack stack;
    
    public FrameBuffer(final FrameBufferStack stack) {
        this.stack = stack;
        registerThisAsAutodeletable();
    }
    
    private boolean isNull() {
        return this.stack == null;
    }
    
    /**
     * Binds this {@link FrameBuffer} to be operated upon.<br>
     * <br>
     * Possible operations are:<br>
     * <ul>
     * <li>Rendering to this FrameBuffer</li>
     * <li>Assigning targets to this FrameBuffer</li>
     * </ul>
     *
     * @see #unbindFrameBuffer()
     */
    public final void bindFrameBuffer() {
        if (isNull()) {
            bindRaw();
        } else {
            this.stack.bind(this);
        }
    }
    
    /**
     * Unbinds this {@link FrameBuffer}.
     *
     * @see #bindFrameBuffer()
     */
    public final void unbindFrameBuffer() {
        if (isNull()) {
            unbindRaw();
        } else {
            this.stack.unbind(this);
        }
    }
    
    public void bindAsTmp() {
        if (isNull()) {
            bindRaw();
        } else {
            this.stack.bindTmp(this);
        }
    }
    
    public void unbindAsTmp() {
        if (isNull()) {
            unbindRaw();
        } else {
            this.stack.unbindTmp();
        }
    }
    
    protected abstract void bindRaw();
    
    protected void unbindRaw() {
    }
    
    /**
     * Sets the {@link FBTarget} at a certain index of this {@link FrameBuffer}.<br>
     * <br>
     * The texture previously located at <code>index</code> will not be destroyed,
     * but might not be accessible through {@link #getTexture(int)} anymore.<br>
     * Note: This function does not bind or unbind this FrameBuffer.<br>
     *
     * @param index  the index where to assign the new target
     * @param target the target
     * @see #assignTargets(int, int, int, FBTarget...)
     * @see #assignTargets(int, FBTarget...)
     * @see #assignTargets(FBTarget...)
     */
    public abstract void assignTarget(int index, @Nonnull FBTarget target);
    
    /**
     * Sets the targets of this {@link FrameBuffer}. All given targets will be set,
     * beginning on the FrameBuffers first target location.
     *
     * @param targets the targets to assign
     * @see #assignTarget(int, FBTarget)
     */
    public void assignTargets(@Nonnull final FBTarget... targets) {
        assignTargets(0, targets);
    }
    
    /**
     * Sets the targets of this {@link FrameBuffer}. All given targets will be set.
     *
     * @param startIndex where to start placing the given targets in the framebuffer
     * @param targets    the targets
     * @see #assignTarget(int, FBTarget)
     */
    public void assignTargets(final int startIndex, @Nonnull final FBTarget... targets) {
        assignTargets(startIndex, 0, targets.length, targets);
    }
    
    /**
     * Sets the targets of this {@link FrameBuffer}.
     *
     * @param startIndex where to start in the FrameBuffer
     * @param srcStart   where to start in the given array
     * @param srcLength  amount of targets
     * @param targets    the targets
     * @see #assignTarget(int, FBTarget)
     *
     */
    public void assignTargets(final int startIndex, final int srcStart, final int srcLength,
            @Nonnull final FBTarget... targets) {
        for (int i = startIndex; i < startIndex + srcLength; i++) {
            assignTarget(i, targets[srcStart + i]);
        }
    }
    
    /**
     * The same as {@link #assignTarget(int, FBTarget)} except this functions binds
     * and unbinds the framebuffer during this operation.
     *
     */
    public void assignTargetB(final int index, @Nonnull final FBTarget target) {
        bindAsTmp();
        assignTarget(index, target);
        unbindAsTmp();
    }
    
    /**
     * The same as {@link #assignTargets(FBTarget...)} except this functions binds
     * and unbinds the framebuffer during this operation.
     *
     */
    public void assignTargetsB(@Nonnull final FBTarget... targets) {
        bindAsTmp();
        assignTargets(0, targets);
        unbindAsTmp();
    }
    
    /**
     * The same as {@link #assignTargets(int, int, int, FBTarget...)} except this
     * functions binds and unbinds the framebuffer during this operation.
     *
     */
    public void assignTargetsB(final int startIndex, final int srcStart, final int srcLength,
            @Nonnull final FBTarget... targets) {
        bindAsTmp();
        assignTargets(startIndex, srcStart, srcLength, targets);
        unbindAsTmp();
    }
    
    /**
     * Returns a {@link Texture} of this {@link FrameBuffer}.
     *
     * @param targetIndex the index of the texture
     * @return the current texture at a certain index
     * @see #assignTarget(int, FBTarget)
     */
    @Nullable
    public abstract Texture getTexture(int targetIndex);
    
    /**
     * Blits the specified attachment of this {@link FrameBuffer} to another
     * FrameBuffer.
     *
     * @param target       the target FrameBuffer
     * @param attachment   the index of the attachment to resolve
     * @param resolveDepth if the depthbuffer should be resolved, too
     */
    public abstract void resolveToFrameBuffer(@Nonnull FrameBuffer target, int attachment);
    
    public void resolveToFrameBuffer(@Nonnull final FrameBuffer target, final FBTarget attachment) {
        resolveToFrameBuffer(target, attachment.attachmentIndex);
    }
    
    /**
     * Draws the texture from the specified index of this {@link FrameBuffer} to the
     * given {@code Batch2D} without any transformation.
     * <p>
     * Note: the {@code Batch2D} will be "bound" and "unbound" by this method.
     * </p>
     *
     * @param targetIndex the texture index
     * @param batch       the Batch2D to draw the texture
     */
    public void renderDirect(final int targetIndex) {
        RendererUtil.renderDirect(getTexture(targetIndex));
    }
    
    //clearing and setting clear color at the same time might be inefficient/redundant. Don't spam the clear functions?
    public void clearDepth() {
        clear(0, 0, 0, 0, SurfaceBufferType.Depth);
    }
    
    public void clearColor() {
        clearColor(0, 0, 0, 0);
    }
    
    public void clearColor(final Color color) {
        clearColor(color.getR(), color.getG(), color.getB(), color.getA());
    }
    
    public void clearColor(final float r, final float g, final float b, final float a) {
        clear(r, g, b, a, SurfaceBufferType.Color);
    }
    
    public void clear(final Color color, final SurfaceBufferType... types) {
        clear(color.getR(), color.getG(), color.getB(), color.getA(), types);
    }
    
    public void clearComplete(Color color) {
        clear(color, SurfaceBufferType.Color, SurfaceBufferType.Depth);   
    }
    
    public void clearComplete() {
        clear(0, 0, 0, 0, SurfaceBufferType.Color, SurfaceBufferType.Depth);
    }
    
    public abstract void clear(float r, float g, float b, float a, SurfaceBufferType... types);
    
    /**
     * THe amount of samples this {@link FrameBuffer} does when rendering onto. 0 if
     * none.
     *
     * @return multisampling value
     */
    public abstract int multisamples();
    
    /**
     * A copy of the current set targets.
     *
     * @return current render targets
     * @see #assignTarget(int, FBTarget)
     */
    @Nonnull
    public abstract FBTarget[] targets();
    
    /**
     * This {@link FrameBuffer} is a RenderBuffer if it has no textures.
     *
     * @return is render buffer?
     */
    public abstract boolean isRenderBuffer();
    
    /**
     * The maximum number of targets this {@link FrameBuffer} can handle
     *
     * @return max number of targets
     */
    public abstract int targetCount();
    
    public abstract FrameBuffer resizedClone(int newWidth, int newHeight);
    
    public FrameBuffer resizedCloneAndDelete(int newWidth, int newHeight) {
        FrameBuffer rC = resizedClone(newWidth, newHeight);
        this.deleteAndUnregister();
        return rC;
    }
    
    public abstract int getWidth();
    
    public abstract int getHeight();
}
