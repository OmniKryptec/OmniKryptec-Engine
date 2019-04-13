/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.util.data.Color;

/**
 * An interface representing a FrameBuffer.<br>
 * FrameBuffers are dynamic textures, e.g. used for postprocessing.
 *
 * @author pcfreak9000
 * @see RenderAPI#createFrameBuffer(int, int, int, int)
 */
public interface FrameBuffer {
    
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
    void assignTarget(int index, @Nonnull FBTarget target);
    
    /**
     * Sets the targets of this {@link FrameBuffer}. All given targets will be set,
     * beginning on the FrameBuffers first target location.
     *
     * @param targets the targets to assign
     * @see #assignTarget(int, FBTarget)
     */
    default void assignTargets(@Nonnull final FBTarget... targets) {
        assignTargets(0, targets);
    }
    
    /**
     * Sets the targets of this {@link FrameBuffer}. All given targets will be set.
     *
     * @param startIndex where to start placing the given targets in the framebuffer
     * @param targets    the targets
     * @see #assignTarget(int, FBTarget)
     */
    default void assignTargets(final int startIndex, @Nonnull final FBTarget... targets) {
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
    default void assignTargets(final int startIndex, final int srcStart, final int srcLength,
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
    default void assignTargetB(int index, @Nonnull FBTarget target) {
        bindFrameBuffer();
        assignTarget(index, target);
        unbindFrameBuffer();
    }
    
    /**
     * The same as {@link #assignTargets(FBTarget...)} except this functions binds
     * and unbinds the framebuffer during this operation.
     * 
     */
    default void assignTargetsB(@Nonnull final FBTarget... targets) {
        bindFrameBuffer();
        assignTargets(0, targets);
        unbindFrameBuffer();
    }
    
    /**
     * The same as {@link #assignTargets(int, int, int, FBTarget...)} except this
     * functions binds and unbinds the framebuffer during this operation.
     * 
     */
    default void assignTargetsB(final int startIndex, final int srcStart, final int srcLength,
            @Nonnull final FBTarget... targets) {
        bindFrameBuffer();
        assignTargets(startIndex, srcStart, srcLength, targets);
        bindFrameBuffer();
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
    void bindFrameBuffer();
    
    /**
     * Unbinds this {@link FrameBuffer}.
     *
     * @see #bindFrameBuffer()
     */
    void unbindFrameBuffer();
    
    /**
     * Returns a {@link Texture} of this {@link FrameBuffer}.
     *
     * @param targetIndex the index of the texture
     * @return the current texture at a certain index
     * @see #assignTarget(int, FBTarget)
     */
    @Nullable
    Texture getTexture(int targetIndex);
    
    /**
     * Blits the specified attachment of this {@link FrameBuffer} to another
     * FrameBuffer.
     *
     * @param target       the target FrameBuffer
     * @param attachment   the index of the attachment to resolve
     * @param resolveDepth if the depthbuffer should be resolved, too
     */
    void resolveToFrameBuffer(@Nonnull FrameBuffer target, int attachment);
    
    //TODO clearing and setting clear color at the same time might be inefficient/redundant
    default void clearDepth() {
        clear(0, 0, 0, 0, SurfaceBufferType.Depth);
    }
    
    default void clearColor() {
        clearColor(0, 0, 0, 0);
    }
    
    default void clearColor(Color color) {
        clearColor(color.getA(), color.getG(), color.getB(), color.getA());
    }
    
    default void clearColor(float r, float g, float b, float a) {
        clear(r, g, b, a, SurfaceBufferType.Color);
    }
    
    default void clear(Color color, SurfaceBufferType... types) {
        clear(color.getA(), color.getG(), color.getB(), color.getA(), types);
    }
    
    void clear(float r, float g, float b, float a, SurfaceBufferType... types);
    
    /**
     * THe amount of samples this {@link FrameBuffer} does when rendering onto. 0 if
     * none.
     *
     * @return multisampling value
     */
    int multisamples();
    
    /**
     * A copy of the current set targets.
     *
     * @return current render targets
     * @see #assignTarget(int, FBTarget)
     */
    @Nonnull
    FBTarget[] targets();
    
    /**
     * This {@link FrameBuffer} is a RenderBuffer if it has no textures.
     *
     * @return is render buffer?
     */
    boolean isRenderBuffer();
    
    /**
     * The maximum number of targets this {@link FrameBuffer} can handle
     *
     * @return max number of targets
     */
    int size();
    
    FrameBuffer resizedClone(int newWidth, int newHeight);
    
    int getWidth();
    
    int getHeight();
}
