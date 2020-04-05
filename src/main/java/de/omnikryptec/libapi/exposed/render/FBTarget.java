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

package de.omnikryptec.libapi.exposed.render;

import java.util.Objects;

/**
 * A class representing the render target of a {@link FrameBuffer}.
 *
 * @author pcfreak9000
 * @see FrameBuffer#assignTarget(int, FBTarget)
 */
public class FBTarget {

    public static enum FBAttachmentFormat {
        RGBA8(4), RGBA16(4), RGBA32(4), DEPTH16(1), DEPTH24(1), DEPTH32(1);

        public final int componentCount;

        private FBAttachmentFormat(final int comps) {
            this.componentCount = comps;
        }
    }

    public static final int DEPTH_ATTACHMENT_INDEX = -1;

    public final FBAttachmentFormat format;
    public final int attachmentIndex;
    public final boolean isDepthAttachment;

    /**
     * Creates a new {@link FBTarget}<br>
     * <br>
     * This constructor is only applicable for the depth attachment.
     *
     * @param format the format of the depth attachment
     */
    public FBTarget(final FBAttachmentFormat format) {
        this(format, DEPTH_ATTACHMENT_INDEX);
    }

    /**
     * Creates a new {@link FBTarget}
     *
     * @param format     the format of the attachment
     * @param attachment the index of the attachment
     */
    public FBTarget(final FBAttachmentFormat format, final int attachment) {
        this.format = format;
        this.attachmentIndex = attachment;
        this.isDepthAttachment = this.attachmentIndex == DEPTH_ATTACHMENT_INDEX;
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
