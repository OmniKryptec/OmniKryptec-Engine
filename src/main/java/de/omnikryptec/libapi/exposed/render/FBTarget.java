package de.omnikryptec.libapi.exposed.render;

import java.util.Objects;

/**
 * A class representing the render target of a {@link FrameBuffer}.
 * 
 * @author pcfreak9000
 * @see FrameBuffer#assignTarget(int, FBTarget)
 */
public class FBTarget {
    
    //TODO texture format?
    public static enum TextureFormat {
        RGBA8(4), RGBA16(4), RGBA32(4), DEPTH16(1), DEPTH24(1), DEPTH32(1);
        
        public final int componentCount;
        
        private TextureFormat(final int comps) {
            this.componentCount = comps;
        }
    }
    
    public final TextureFormat format;
    public final int attachmentIndex;
    //TODO make enum?:
    public final boolean isDepthAttachment;
    
    //TODO support for TextureData
    
    /**
     * Creates a new {@link FBTarget}<br>
     * <br>
     * This constructor is only applicable for the depth attachment.
     * 
     * @param format the format of the depth attachment
     */
    public FBTarget(final TextureFormat format) {
        this(format, -1);
    }
    
    /**
     * Creates a new {@link FBTarget}
     * 
     * @param format     the format of the attachment
     * @param attachment the index of the attachment
     */
    public FBTarget(final TextureFormat format, final int attachment) {
        this.format = format;
        this.attachmentIndex = attachment;
        this.isDepthAttachment = this.attachmentIndex == -1;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof FBTarget) {
            final FBTarget other = (FBTarget) obj;
            if (other.attachmentIndex == this.attachmentIndex && other.format == this.format
                    && other.isDepthAttachment == other.isDepthAttachment) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.format, this.attachmentIndex, this.isDepthAttachment);
    }
    
}