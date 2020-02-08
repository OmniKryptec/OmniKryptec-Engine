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

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.openal.AL10;

public class Sound extends ALSound {
    
    public final int bufferID;
    
    private final int size;
    private float length;
    
    public Sound(AudioFormat format, ByteBuffer data) {
        super(format, SoundType.CACHED);
        this.bufferID = AL10.alGenBuffers();
        AL10.alBufferData(bufferID, getOpenALFormat(), data, getFrequency());
        this.size = AL10.alGetBufferi(bufferID, AL10.AL_SIZE);
        calculateLength();
    }
    
    private final float calculateLength() {
        length = (((size) * 8.0F) / (((float) getChannels()) * ((float) getBits()))) / (getFrequency());
        return length;
    }
    
    public final float getLength() {
        return length;
    }

    @Override
    public final String toString() {
        return String.format(
                "Sound [bufferdID = %d, size = %d, channels = %d, bits = %d, frequency = %d, length = %.2f]", bufferID,
                size, getChannels(), getBits(), getFrequency(), length);
    }
    
    @Override
    void attach(AudioSource source) {
        AL10.alSourcei(source.getSourceID(), AL10.AL_BUFFER, bufferID);
    }
    
    @Override
    void detach() {
    }
    
    @Override
    public void deleteRaw() {
        AL10.alDeleteBuffers(bufferID);
    }
    
}
