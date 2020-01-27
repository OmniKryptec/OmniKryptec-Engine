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

import java.lang.reflect.Field;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.Logger;

/**
 *
 * @author Panzer1119 & pcfreak9000
 */
public class OpenALUtil {
    
    private static final Logger logger = Logger.getLogger(OpenALUtil.class);
    
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
    
    public static int booleanToOpenAL(boolean b) {
        return b ? AL10.AL_TRUE : AL10.AL_FALSE;
    }
    
    public static void flushErrors() {
        int e = 0;
        int found = 0;
        while ((e = AL10.alGetError()) != AL10.AL_NO_ERROR) {
            logger.error("OpenAL error: " + (LibAPIManager.debug() ? searchConstants(e) : e));
            found++;
        }
        if (found != 0) {
            throw new RuntimeException("Stopping due to " + found + " OpenAL error(s)");
        }
        if (found == 0) {
            logger.debug("No OpenAL errors found!");
        }
    }
    
    private static final Class<?>[] constantsClasses = { AL10.class, AL11.class };
    
    private static String searchConstants(final int i) {
        for (final Class<?> c : constantsClasses) {
            final Field[] fields = c.getFields();
            for (final Field f : fields) {
                try {
                    if (i == f.getInt(null)) {
                        return f.getName();
                    }
                } catch (final IllegalArgumentException e) {
                } catch (final IllegalAccessException e) {
                }
            }
        }
        throw new IllegalArgumentException("Constant with value '" + i + "' not found");
    }
}
