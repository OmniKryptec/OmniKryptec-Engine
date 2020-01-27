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

import javax.sound.sampled.AudioFormat;

import org.lwjgl.openal.AL10;

import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.openal.ALSound.SoundType;

/**
 * Cached sound
 *
 * @author Panzer1119
 */
public class Sound extends ALSound {
    
    private final String name;
   
    private final int bufferID;
    
    private final int size;
    private float length;
    
    /**
     * Creates a new Sound
     *
     * @param name     String Name of the Sound
     * @param bufferID Integer BufferID
     */
    public Sound(String name, AudioFormat format) {//TODO load sounds (transfer stuff from AudioManager)
        super(format, SoundType.CACHED);
        this.name = name;
        this.bufferID = AL10.alGenBuffers();
        this.size = AL10.alGetBufferi(bufferID, AL10.AL_SIZE);
        calculateLength();
        registerThisAsAutodeletable();
    }
    
    private final float calculateLength() {
        length = (((size) * 8.0F) / (((float) getChannels()) * ((float) getBits()))) / (getFrequency());
        return length;
    }
    
    public final String getName() {
        return name;
    }
    
    public final float getLength() {
        return length;
    }
    
    /**
     * Sets the frequency
     *
     * @param frequency Integer Frequency
     * @return Sound A reference to this Sound
     */
    @Deprecated
    public final Sound setFrequency(int frequency) {
        //this.frequency = frequency;
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
                size, getChannels(), getBits(), getFrequency(), length);
    }
    
 
    @Override
    void attach(AudioSource source) {
        loadToAudioSource(source);
    }

    @Override
    void detach() {        
    }
    
    @Override
    public void deleteRaw() {
        AL10.alDeleteBuffers(bufferID);
    }
    
}
