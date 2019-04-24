package de.omnikryptec.libapi.exposed.window;

import org.joml.Vector2dc;

import de.omnikryptec.libapi.exposed.AutoDeletionManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.FrameBufferStack;

public abstract class SurfaceBuffer extends FrameBuffer {
    
    public SurfaceBuffer(FrameBufferStack stack) {
        super(stack);
        AutoDeletionManager.unregister(this);
    }
    
    public abstract int[] viewport();
    
    public boolean isInViewport(Vector2dc vec) {
        return isInViewport(vec.x(), vec.y());
    }
    
    public boolean isInViewport(double x, double y) {
        int[] viewport = viewport();
        return x > viewport[0] && x < viewport[2] + viewport[0] && y > viewport[1] && y < viewport[3] + viewport[1];
    }
}
