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

package de.omnikryptec.resource;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class TextureData {
    
    public static TextureData decode(final InputStream inputstream) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            final PNGDecoder decoder = new PNGDecoder(inputstream);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = BufferUtils.createByteBuffer(4 * width * height);
            decoder.decode(buffer, width * 4, Format.BGRA);
            buffer.flip();
            return new TextureData(buffer, width, height);
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                inputstream.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private final int width;
    private final int height;
    private final ByteBuffer buffer;
    
    public TextureData(final ByteBuffer buffer, final int width, final int height) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public ByteBuffer getBuffer() {
        return this.buffer;
    }
}
