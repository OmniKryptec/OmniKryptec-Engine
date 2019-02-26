package de.omnikryptec.libapi.exposed.window;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;

public interface SurfaceBuffer extends FrameBuffer{
    
    int[] viewport();
    //double aspectRatio();
}
