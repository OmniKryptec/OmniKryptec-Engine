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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Instant;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.openal.ALSound.SoundType;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Util;

/**
 * Streamed sound
 *
 * @author Panzer1119
 */
public class StreamedSound extends ALSound {
    
    private static final Logger LOGGER = Logger.getLogger(StreamedSound.class);
    
    public static final int STANDARD_BUFFER_COUNT = 3;
    public static final int STANDARD_BUFFER_LENGTH = 1000;
    
    //    private final ArrayList<Integer> buffersCreated = new ArrayList<>();
    private final String name;
    private AudioSource source;
    private final AudioInputStream audioInputStream;
    private final ByteBuffer pcm;
    private final int bufferCount;
    private final int bufferTime;
    private double lastTime = 0;
    
    private Instant lastTimeT = Instant.now();
    
    /**
     * Creates a StreamedSound Object
     *
     * @param name             Name of the Sound
     * @param source           AudioSource component
     * @param audioInputStream AudioInputStream
     */
    public StreamedSound(String name, AudioInputStream audioInputStream) {
        this(name, audioInputStream, STANDARD_BUFFER_COUNT, STANDARD_BUFFER_LENGTH);
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
    public StreamedSound(String name, AudioInputStream audioInputStream, int bufferCount, int bufferTime) {
        super(audioInputStream.getFormat(), SoundType.STREAM);
        this.name = name;
        this.audioInputStream = audioInputStream;
        this.bufferCount = bufferCount;
        this.bufferTime = bufferTime;
        this.pcm = BufferUtils.createByteBuffer((int) (audioInputStream.getFormat().getSampleRate() * 4 * (this.bufferTime / 1000)));
    }
    
    private final void initBuffers(AudioSource source) {
        for (int i = 0; i < bufferCount; i++) {
            final int bufferID = AL10.alGenBuffers();
            //            buffersCreated.add(bufferID);
            final boolean refilled = refillBuffer();
            AL10.alBufferData(bufferID, getOpenALFormat(), pcm, getFrequency());
            AL10.alSourceQueueBuffers(source.getSourceID(), bufferID);
        }
    }
    
    private final void deleteBuffers(AudioSource source) {
        int bufferRemovedID = 0;
        while ((bufferRemovedID = AL10.alSourceUnqueueBuffers(source.getSourceID())) != 0) {
            AL10.alDeleteBuffers(bufferRemovedID);
        }
        //        for (int bufferID : buffersCreated) {
        //            AL10.alDeleteBuffers(bufferID);
        //        }
        //        buffersCreated.clear();
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
        if (Util.ensureNonNull(source) != this.source) {
            throw new IllegalStateException("Can't stream the same sound twice");
        }
        initBuffers(source);
        this.source = source;
    }
    
    @Override
    final void detach() {
        if (this.source == null) {
            throw new IllegalStateException("No source attached");
        }
        deleteBuffers(this.source);
        this.source = null;
    }
    

    public final String getName() {
        return name;
    }
    
    public final void update(double currentTime) {
        //        if (buffersCreated.isEmpty()) {
        //            return;
        //        }
        final double duration = currentTime - lastTime;
        lastTime = currentTime;
        int bufferProcessedID = 0;
        while ((bufferProcessedID = AL10.alGetSourcei(source.getSourceID(), AL10.AL_BUFFERS_PROCESSED)) != 0) {
            final int bufferRemovedID = AL10.alSourceUnqueueBuffers(source.getSourceID());
            final boolean refilled = refillBuffer();
            if (refilled) {
                AL10.alBufferData(bufferRemovedID, getOpenALFormat(), pcm, getFrequency());
                AL10.alSourceQueueBuffers(source.getSourceID(), bufferRemovedID);
            } else {
                LOGGER.warn("Buffer could not be refilled!");
            }
        }
        if (!source.isPlaying()) {
            source.stop();
        }
    }
    
    private final boolean refillBuffer() {
        try {
            Instant now = Instant.now();
            lastTimeT = now;
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
    
    /**
     * Creates a new StreamSound of an ofAdvancedFile
     *
     * @param name   String Name of the Sound
     * @param source AudioSource Destination for this StreamedSound
     * @param file   ofAdvancedFile File to load from
     * @return StreamedSound Sound
     */
    public static final StreamedSound ofAdvancedFile(String name, AudioSource source, AdvancedFile file) {
        return ofInputStream(name, source, file.createInputStream());
    }
    
    /**
     * Creates a new StreamSound of an InputStream
     *
     * @param name        String Name of the Sound
     * @param source      AudioSource Destination for this StreamedSound
     * @param inputStream InputStream Stream to load from
     * @return StreamedSound Sound
     */
    public static final StreamedSound ofInputStream(String name, AudioSource source, InputStream inputStream) {
        return ofInputStream(name, source, inputStream, STANDARD_BUFFER_COUNT, STANDARD_BUFFER_LENGTH);
    }
    
    /**
     * Creates a new StreamSound of an InputStream
     *
     * @param name        String Name of the Sound
     * @param source      AudioSource Destination for this StreamedSound
     * @param inputStream InputStream Stream to load from
     * @param bufferCount Integer Number of used Buffers
     * @param bufferTime  Integer Buffered time in milliseconds
     * @return StreamedSound Sound
     */
    public static final StreamedSound ofInputStream(String name, AudioSource source, InputStream inputStream,
            int bufferCount, int bufferTime) {
        try {
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            return new StreamedSound(name, audioInputStream, bufferCount, bufferTime);
        } catch (Exception ex) {
            LOGGER.error("Error while creating StreamedSound of InputStream", ex);
            return null;
        }
    }
    
    @Override
    public void deleteRaw() {
        try {
            audioInputStream.close();
        } catch (IOException e) {
            LOGGER.warn("Could not deleted StreamedSound", e);
        }
    }
    
}
