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

package de.omnikryptec.libapi.exposed.window;

import de.omnikryptec.libapi.exposed.AutoDeletionManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.FrameBufferStack;
import org.joml.Vector2dc;

import java.util.Arrays;

public abstract class SurfaceBuffer extends FrameBuffer {
    
    public SurfaceBuffer(final FrameBufferStack stack) {
        super(stack);
        AutoDeletionManager.unregister(this);
    }
    
    /**
     * The result of this method call should be considered read-only. (You could
     * mess with it though!)
     *
     * @return viewport
     */
    public abstract int[] getViewportUnsafe();
    
    public int[] getViewport() {
        final int[] vp = getViewportUnsafe();
        return Arrays.copyOf(vp, vp.length);
    }
    
    public boolean isInViewport(final Vector2dc vec) {
        return isInViewport(vec.x(), vec.y());
    }
    
    public boolean isInViewport(final double x, final double y) {
        final int[] viewport = getViewportUnsafe();
        return x > viewport[0] && x < viewport[2] + viewport[0] && y > viewport[1] && y < viewport[3] + viewport[1];
    }
}
