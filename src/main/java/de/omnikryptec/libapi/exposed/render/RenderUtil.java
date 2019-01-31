package de.omnikryptec.libapi.exposed.render;

public class RenderUtil {
    
    public static void bindIfNonNull(FrameBuffer frameBuffer) {
        if (frameBuffer != null) {
            frameBuffer.bindFrameBuffer();
        }
    }
    
    public static void unbindIfNonNull(FrameBuffer frameBuffer) {
        if (frameBuffer != null) {
            frameBuffer.unbindFrameBuffer();
        }
    }
    
    public static FrameBuffer cloneAndResize(FrameBuffer frameBuffer, int newWidth, int newHeight) {
        return frameBuffer != null
                ? RenderAPI.get().createFrameBuffer(newWidth, newHeight, frameBuffer.multisamples(),
                        frameBuffer.targets())
                : null;
    }
    
}
