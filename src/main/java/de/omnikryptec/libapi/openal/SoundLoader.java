package de.omnikryptec.libapi.openal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;

public class SoundLoader {
    
    public static final Sound loadSound(InputStream inputStream) {
        try {
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            if (audioInputStream == null) {
                throw new IllegalArgumentException("Couldn't create AudioInputStream");
            }
            final AudioFormat audioFormat = audioInputStream.getFormat();
            if (audioFormat.isBigEndian()) {
                throw new UnsupportedAudioFileException("Can not handle Big Endian formats yet"); // TODO Implement Big Endian formats
            }
            final byte[] bytes = IOUtils.toByteArray(audioInputStream);
            final ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
            data.flip();
            final Sound sound = new Sound(audioFormat, data);
            return sound;
        } catch (IOException | UnsupportedAudioFileException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Creates a new StreamSound of an InputStream
     *
     * @param inputStream InputStream Stream to load from
     * @param bufferCount Integer Number of used Buffers
     * @param bufferTime  Integer Buffered time in milliseconds
     * @return StreamedSound Sound
     */
    public static final StreamedSound streamSound(InputStream stream, int bufferCount, int bufferTime) {
        try {
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
            return new StreamedSound(audioInputStream, bufferCount, bufferTime);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
