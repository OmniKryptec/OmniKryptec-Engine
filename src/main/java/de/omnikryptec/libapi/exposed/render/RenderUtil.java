package de.omnikryptec.libapi.exposed.render;

public class RenderUtil {

    public static void bindIfNonNull(final FrameBuffer frameBuffer) {
        if (frameBuffer != null) {
            frameBuffer.bindFrameBuffer();
        }
    }

    public static void unbindIfNonNull(final FrameBuffer frameBuffer) {
        if (frameBuffer != null) {
            frameBuffer.unbindFrameBuffer();
        }
    }

    public static FrameBuffer cloneAndResize(final FrameBuffer frameBuffer, final int newWidth, final int newHeight,
            boolean blitContents) {
        if (frameBuffer == null) {
            return null;
        }
        return frameBuffer.resizedClone(newWidth, newHeight);
    }

}
