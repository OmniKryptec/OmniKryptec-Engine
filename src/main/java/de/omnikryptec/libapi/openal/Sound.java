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

import org.lwjgl.openal.AL10;

import de.omnikryptec.libapi.exposed.Deletable;

/**
 * Cached sound
 *
 * @author Panzer1119
 */
public class Sound implements ISound, Deletable {
    
    private final String name;
    private final int bufferID;
    private final int size;
    private final int channels;
    private final int bits;
    private int frequency;
    private float length;
    
    /**
     * Creates a new Sound
     *
     * @param name     String Name of the Sound
     * @param bufferID Integer BufferID
     */
    public Sound(String name, int bufferID) {
        this.name = name;
        this.bufferID = bufferID;
        this.size = AL10.alGetBufferi(bufferID, AL10.AL_SIZE);
        this.channels = AL10.alGetBufferi(bufferID, AL10.AL_CHANNELS);
        this.bits = AL10.alGetBufferi(bufferID, AL10.AL_BITS);
        this.frequency = AL10.alGetBufferi(bufferID, AL10.AL_FREQUENCY);
        calculateLength();
        registerThisAsAutodeletable();
    }
    
    private final float calculateLength() {
        length = (((size) * 8.0F) / (((float) channels) * ((float) bits))) / (frequency);
        return length;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    @Override
    public final int getBufferID() {
        return bufferID;
    }
    
    @Override
    public final int getSize() {
        return size;
    }
    
    @Override
    public final int getChannels() {
        return channels;
    }
    
    @Override
    public final int getBits() {
        return bits;
    }
    
    @Override
    public final int getFrequency() {
        return frequency;
    }
    
    @Override
    public final float getLength() {
        return length;
    }
    
    /**
     * Sets the frequency
     *
     * @param frequency Integer Frequency
     * @return Sound A reference to this Sound
     */
    public final Sound setFrequency(int frequency) {
        this.frequency = frequency;
        calculateLength();
        return this;
    }
    
    /**
     * Loads this Sound to an AudioSource
     *
     * @param source AudioSource Source to be loaded to
     * @return Sound A reference to this Sound
     */
    public final Sound loadToAudioSource(AudioSource source) {
        AL10.alSourcei(source.getSourceID(), AL10.AL_BUFFER, bufferID);
        return this;
    }
    
    @Override
    public final String toString() {
        return String.format(
                "Sound [bufferdID = %d, size = %d, channels = %d, bits = %d, frequency = %d, length = %.2f]", bufferID,
                size, channels, bits, frequency, length);
    }
    
    @Override
    public SoundType getType() {
        return SoundType.CACHED;
    }
    
    @Override
    public int getOpenALFormat() {
        return OpenALUtil.audioFormatToOpenALFormat(channels, bits);
    }
    
    @Override
    public boolean play(AudioSource source) {
        loadToAudioSource(source);
        return true;
    }
    
    @Override
    public boolean stop(AudioSource source) {
        return false;
    }
    
    @Override
    public void update(double currentTime) {
    }

    @Override
    public void deleteRaw() {
        AL10.alDeleteBuffers(bufferID);
    }
    
}
