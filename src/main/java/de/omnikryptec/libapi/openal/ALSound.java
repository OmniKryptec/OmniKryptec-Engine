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

import de.omnikryptec.libapi.exposed.Deletable;

public abstract class ALSound implements Deletable {
    /**
     * Sound type (cached or streamed)
     */
    public static enum SoundType {
        CACHED, STREAM;
    }
    
    private final AudioFormat audioFormat;
    private final int formatId;
    private final SoundType soundType;
    
    public ALSound(AudioFormat format, SoundType type) {
        this.audioFormat = format;
        this.formatId = OpenALUtil.audioFormatToOpenALFormat(this.audioFormat);
        this.soundType = type;
        registerThisAsAutodeletable();
    }
    
    abstract void attach(AudioSource as);
    
    abstract void detach();
    
    public SoundType getType() {
        return this.soundType;
    }
    
    public final int getChannels() {
        return this.audioFormat.getChannels();
    }
    
    public final int getBits() {
        return this.audioFormat.getSampleSizeInBits();
    }
    
    public final int getFrequency() {
        return (int) this.audioFormat.getSampleRate();
    }
    
    public int getOpenALFormat() {
        return this.formatId;
    }
}
