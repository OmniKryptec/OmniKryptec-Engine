package de.omnikryptec.libapi.exposed.render;

import java.util.Objects;

public class FBTarget {

    //TODO texture format?
    public static enum TextureFormat {
        RGBA8(4), DEPTH16(1), DEPTH24(1), DEPTH32(1);

        public final int componentCount;

        private TextureFormat(final int comps) {
            this.componentCount = comps;
        }
    }

    public final TextureFormat format;
    public final int attachmentIndex;
    //TODO make enum:
    public final boolean isDepthAttachment;

    public FBTarget(final TextureFormat format) {
        this(format, -1);
    }

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
