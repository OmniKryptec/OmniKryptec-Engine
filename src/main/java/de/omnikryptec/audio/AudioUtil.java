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

package de.omnikryptec.audio;

import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;

/**
 *
 * @author Panzer119
 */
public class AudioUtil {
    
    public static final int MONO = 1;
    public static final int STEREO = 2;
    
    public static final int audioFormatToOpenALFormat(AudioFormat audioFormat) {
        return audioFormatToOpenALFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
    }
    
    public static final int audioFormatToOpenALFormat(int channels, int sampleSizeInBits) {
        int openALFormat = -1;
        switch (channels) {
        case MONO:
            switch (sampleSizeInBits) {
            case 8:
                openALFormat = AL10.AL_FORMAT_MONO8;
                break;
            case 16:
                openALFormat = AL10.AL_FORMAT_MONO16;
                break;
            }
            break;
        case STEREO:
            switch (sampleSizeInBits) {
            case 8:
                openALFormat = AL10.AL_FORMAT_STEREO8;
                break;
            case 16:
                openALFormat = AL10.AL_FORMAT_STEREO16;
                break;
            }
            break;
        }
        return openALFormat;
    }
    
}
