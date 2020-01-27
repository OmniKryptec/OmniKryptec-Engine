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

/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
 */
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
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALCCapabilities;

import de.codemakers.io.file.AdvancedFile;

/**
 * Main audio manager class
 *
 * @author Panzer1119
 */
public class AudioManager {

    /**
     * Initializes the OpenAL AudioSystem
     *
     * @return <tt>true</tt> if the AudioSystem was successfully initialized
     */
    
    /**
     * Loads a sound from a File to the static Soundbuffer
     *
     * @param name String Name of the Sound
     * @param file AdvancedFile AdvancedFile where the Sound is saved
     * @return Integer BufferID where the sound was saved
     */
    public static final int loadSound(String name, AdvancedFile file) {
        return loadSound(name, file.createInputStream());
    }
    
    /**
     * Loads a sound from a Path within a jar to the static Soundbuffer
     *
     * @param name String Name of the Sound
     * @param path String Path in the jar where the Sound is saved
     * @return Integer BufferID where the sound was saved
     */
    public static final int loadSound(String name, String path) {
        return loadSound(name, AudioManager.class.getResourceAsStream(path));
    }
    

    
    //    /**
    //     * Loads a sound from an InputStream to the static Soundbuffer
    //     *
    //     * @param name        String Name of the Sound
    //     * @param inputStream InputStream Stream where should be read from
    //     * @return Integer BufferID where the sound was saved
    //     */
    //    @Deprecated
    //    public static final int loadSoundOLD(String name, InputStream inputStream) {
    //        deleteSound(name);
    //        final int bufferID = AL10.alGenBuffers();
    //        // final WaveData waveData = WaveData.create(inputStream);
    //        // AL10.alBufferData(bufferID, waveData.format, waveData.data,
    //        // waveData.samplerate);
    //        // waveData.dispose();
    //        final Sound sound = new Sound(name, bufferID);
    //        sounds.add(sound);
    //        return bufferID;
    //    }

}
