package de.omnikryptec.resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class TextureData {
    
    public static TextureData decode(InputStream inputstream) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            PNGDecoder decoder = new PNGDecoder(inputstream);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = BufferUtils.createByteBuffer(4 * width * height);
            decoder.decode(buffer, width * 4, Format.BGRA);
            buffer.flip();
            return new TextureData(buffer, width, height);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                inputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private int width;
    private int height;
    private ByteBuffer buffer;
    
    public TextureData(ByteBuffer buffer, int width, int height) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public ByteBuffer getBuffer() {
        return buffer;
    }
}
