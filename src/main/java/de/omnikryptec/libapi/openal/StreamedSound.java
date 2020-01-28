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

package de.omnikryptec.libapi.openal;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioInputStream;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Util;

/**
 * Streamed sound
 *
 * @author Panzer1119 & pcfreak9000
 */
public class StreamedSound extends ALSound {
    
    private static final Logger LOGGER = Logger.getLogger(StreamedSound.class);
    
    public static final int STANDARD_BUFFER_COUNT = 3;
    public static final int STANDARD_BUFFER_LENGTH = 1000;
    
    private final AudioInputStream audioInputStream;
    private final ByteBuffer pcm;
    private final int bufferCount;
    private final int bufferTime;
    
    private AudioSource source;
    
    /**
     * Creates a StreamedSound Object
     *
     * @param name             Name of the Sound
     * @param source           AudioSource component
     * @param audioInputStream AudioInputStream
     */
    public StreamedSound(AudioInputStream audioInputStream) {
        this(audioInputStream, STANDARD_BUFFER_COUNT, STANDARD_BUFFER_LENGTH);
    }
    
    /**
     * Creates a StreamedSound Object
     *
     * @param name             Name of the Sound
     * @param source           AudioSource component
     * @param audioInputStream AudioInputStream
     * @param bufferCount      Number of used Buffers
     * @param bufferTime       Buffered time in milliseconds
     */
    public StreamedSound(AudioInputStream audioInputStream, int bufferCount, int bufferTime) {
        super(audioInputStream.getFormat(), SoundType.STREAM);
        this.audioInputStream = audioInputStream;
        this.bufferCount = bufferCount;
        this.bufferTime = bufferTime;
        this.pcm = BufferUtils
                .createByteBuffer((int) (audioInputStream.getFormat().getSampleRate() * 4 * (this.bufferTime / 1000)));
    }
    
    private final void initBuffers(AudioSource source) {
        for (int i = 0; i < bufferCount; i++) {
            final int bufferID = AL10.alGenBuffers();
            final boolean refilled = refillBuffer();
            if (refilled) {
                AL10.alBufferData(bufferID, getOpenALFormat(), pcm, getFrequency());
                AL10.alSourceQueueBuffers(source.getSourceID(), bufferID);
            } else {
                LOGGER.warn("Buffer could not be filled");
            }
        }
    }
    
    private final void deleteBuffers(AudioSource source) {
        int bufferRemovedID = 0;
        while ((bufferRemovedID = AL10.alSourceUnqueueBuffers(source.getSourceID())) != 0) {
            AL10.alDeleteBuffers(bufferRemovedID);
        }
    }
    
    /**
     * Returns the AudioInputStream
     *
     * @return AudioInputStream Audiostream
     */
    public final AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }
    
    @Override
    final void attach(AudioSource source) {
        if (Util.ensureNonNull(source) != this.source && this.source != null) {
            throw new IllegalStateException("Can't stream the same sound twice");
        }
        initBuffers(source);
        OpenAL.ACTIVE_STREAMED_SOUNDS.add(this);
        this.source = source;
    }
    
    @Override
    final void detach() {
        if (this.source == null) {
            throw new IllegalStateException("No source attached");
        }
        deleteBuffers(this.source);
        OpenAL.ACTIVE_STREAMED_SOUNDS.remove(this);
        this.source = null;
    }
    
    final void update() {
        while ((AL10.alGetSourcei(source.getSourceID(), AL10.AL_BUFFERS_PROCESSED)) != 0) {
            final int bufferRemovedID = AL10.alSourceUnqueueBuffers(source.getSourceID());
            final boolean refilled = refillBuffer();
            if (refilled) {
                AL10.alBufferData(bufferRemovedID, getOpenALFormat(), pcm, getFrequency());
                AL10.alSourceQueueBuffers(source.getSourceID(), bufferRemovedID);
            } else {
                LOGGER.warn("Buffer could not be refilled");
            }
        }
        //wat macht das denn hier?!
        if (!source.isPlaying()) {
            source.stop();
        }
    }
    
    private final boolean refillBuffer() {
        try {
            pcm.clear();
            final byte[] data = new byte[pcm.capacity()];
            final int read = audioInputStream.read(data);
            pcm.put(data);
            pcm.flip();
            return read >= 0;
        } catch (Exception ex) {
            LOGGER.warn("Error while refilling buffer", ex);
            return false;
        }
    }
    
    @Override
    public void deleteRaw() {
        try {
            audioInputStream.close();
        } catch (IOException e) {
            LOGGER.warn("Could not delete StreamedSound", e);
        }
    }
    
}
