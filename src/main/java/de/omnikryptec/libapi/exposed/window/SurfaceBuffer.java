package de.omnikryptec.libapi.exposed.window;

import java.util.Arrays;

import org.joml.Vector2dc;

import de.omnikryptec.libapi.exposed.AutoDeletionManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.FrameBufferStack;

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
