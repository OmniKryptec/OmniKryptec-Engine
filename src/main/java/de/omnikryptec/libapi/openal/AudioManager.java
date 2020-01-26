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

import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.IOUtils;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import de.codemakers.base.logger.LogLevel;
import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.libapi.exposed.LibAPIManager;

/**
 * Main audio manager class
 *
 * @author Panzer1119
 */
public class AudioManager {
    
    private static DistanceModel distanceModel = null;
    private static boolean isInitialized = false;
    private static long defaultDevice = -1;
    private static long context = -1;
    private static ALCCapabilities deviceCapabilities = null;
    
    /**
     * Initializes the OpenAL AudioSystem
     *
     * @return <tt>true</tt> if the AudioSystem was successfully initialized
     */
    public static final boolean init() {
        if (isInitialized) {
            if (Logger.isDebugMode()) {
                Logger.log("Audio is already initialized!", LogLevel.INFO);
            }
            return false;
        }
        try {
            defaultDevice = ALC10.alcOpenDevice((ByteBuffer) null);
            deviceCapabilities = ALC.createCapabilities(defaultDevice);
            final IntBuffer contextAttributeList = BufferUtils.createIntBuffer(16);
            contextAttributeList.put(ALC10.ALC_REFRESH);
            contextAttributeList.put(60);
            contextAttributeList.put(ALC10.ALC_SYNC);
            contextAttributeList.put(ALC10.ALC_FALSE);
            contextAttributeList.put(ALC_MAX_AUXILIARY_SENDS);
            contextAttributeList.put(2);
            contextAttributeList.put(0);
            contextAttributeList.flip();
            context = ALC10.alcCreateContext(defaultDevice, contextAttributeList);
            if (!ALC10.alcMakeContextCurrent(context)) {
                throw new Exception("Failed to set current OpenAL Context!");
            }
            AL.createCapabilities(deviceCapabilities);
            setDistanceModel(DistanceModel.EXPONENT_CLAMPED);
            isInitialized = true;
            Logger.log("Successfully initialized the Audiosystem!", LogLevel.FINEST);
            return true;
        } catch (Exception ex) {
            isInitialized = false;
            Logger.logErr("Error while initializing the sound library: " + ex, ex);
            return false;
        }
    }

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
    
    public static final int loadSound(String name, InputStream inputStream) {
        try {
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            if (audioInputStream == null) {
                return 0;
            }
            final AudioFormat audioFormat = audioInputStream.getFormat();
            if (audioFormat.isBigEndian()) {
                throw new UnsupportedAudioFileException("Can not handle Big Endian formats yet"); // TODO Implement Big
                                                                                                  // Endian formats
            }
            final int openALFormat = OpenALUtil.audioFormatToOpenALFormat(audioFormat);
            final byte[] bytes = IOUtils.toByteArray(audioInputStream);
            final ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
            data.flip();
            deleteSound(name);
            final int bufferID = AL10.alGenBuffers();
            AL10.alBufferData(bufferID, openALFormat, data, (int) audioFormat.getSampleRate());
            final Sound sound = new Sound(name, bufferID);
            sound.setFrequency((int) audioFormat.getFrameRate());
            sounds.add(sound);
            return bufferID;
        } catch (Exception ex) {
            Logger.logErr("Error while loading a Sound: " + ex, ex);
            return 0;
        }
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
//    
    /**
     * Returns a Sound for the given name
     *
     * @param name String Name of the Sound
     * @return Sound Found Sound or null
     */
    public static final Sound getSound(String name) {
        for (Sound sound : sounds) {
            if (sound.getName().equals(name)) {
                return sound;
            }
        }
        return null;
    }
    
    /**
     * Returns all names of the loaded Sounds
     *
     * @return String Array with all Sound names
     */
    public static final String[] getSoundNames() {
        final String[] names = new String[sounds.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = sounds.get(i).getName();
        }
        return names;
    }
    
    /**
     * Returns a Sound for the given bufferID
     *
     * @param bufferID Integer BufferID
     * @return Sound Found Sound or null
     */
    public static final Sound getSound(int bufferID) {
        for (Sound sound : sounds) {
            if (sound.getBufferID() == bufferID) {
                return sound;
            }
        }
        return null;
    }

    
    static {
        LibAPIManager.registerResourceShutdownHooks(() -> cleanup());
    }
    
    /**
     * Cleans up every Sound and destroys the OpenAL AudioSystem
     */
    private static final void cleanup() {
        ALC10.alcMakeContextCurrent(0);
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(defaultDevice);
    }
    
    /**
     * Updates all StreamedSounds
     *
     * @param currentTime Long Current time in milliseconds
     */
    public static final void update(double currentTime) {
        for (StreamedSound streamedSound : StreamedSound.streamedSounds) {
            streamedSound.update(currentTime);
        }
    }

}
