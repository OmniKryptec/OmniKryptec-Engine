package de.omnikryptec.libapi.exposed.window;

import de.omnikryptec.libapi.exposed.AutoDeletionManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.FrameBufferStack;

public abstract class SurfaceBuffer extends FrameBuffer{
    
    public SurfaceBuffer(FrameBufferStack stack) {
        super(stack);        
        AutoDeletionManager.unregister(this);
    }

    public abstract int[] viewport();
}
