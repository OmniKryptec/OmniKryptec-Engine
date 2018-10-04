/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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
package de.omnikryptec.old.audio;

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

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.old.gameobject.component.Component;
import de.omnikryptec.old.util.AudioUtil;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

/**
 * Main audio manager class
 *
 * @author Panzer1119
 */
public class AudioManager {

    private static final ArrayList<Sound> sounds = new ArrayList<>();
    private static DistanceModel distanceModel = null;
    private static boolean isInitialized = false;
    private static Component<?> blockingAudioListenerComponent = null;
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
     * Sets the data for the listener of the AudioSystem
     *
     * @param component Component Component which sets the data
     * @param position Vector3f Vector of the position
     * @param rotation Vector3f Vector of the rotation
     * @param velocity Vector3f Vector of the velocity
     * @return <tt>true</tt> if the data was successfully changed
     */
    public static final boolean setListenerData(Component<?> component, javax.vecmath.Vector3f position,
            javax.vecmath.Vector3f rotation, javax.vecmath.Vector3f velocity) {
        return setListenerData(component, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z,
                velocity.x, velocity.y, velocity.z);
    }

    /**
     * Sets the data for the listener of the AudioSystem
     *
     * @param component Component Component which sets the data
     * @param position Vector3f Vector of the position
     * @param rotation Vector3f Vector of the rotation
     * @param velocity Vector3f Vector of the velocity
     * @return <tt>true</tt> if the data was successfully changed
     */
    public static final boolean setListenerData(Component<?> component, Vector3f position, Vector3f rotation,
            Vector3f velocity) {
        return setListenerData(component, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z,
                velocity.x, velocity.y, velocity.z);
    }

    /**
     * Sets the data for the listener of the AudioSystem
     *
     * @param component Component Component which sets the data
     * @param posX Float Float of the x-position
     * @param posY Float Float of the y-position
     * @param posZ Float Float of the z-position
     * @param rotX Float Float of the x-rotation
     * @param rotY Float Float of the y-rotation
     * @param rotZ Float Float of the z-rotation
     * @param velX Float Float of the x-velocity
     * @param velY Float Float of the y-velocity
     * @param velZ Float Float of the z-velocity
     * @return <tt>true</tt> if the data was successfully changed
     */
    public static final boolean setListenerData(Component<?> component, float posX, float posY, float posZ, float rotX,
            float rotY, float rotZ, float velX, float velY, float velZ) {
        if (blockingAudioListenerComponent != null
                && (component == null || blockingAudioListenerComponent != component)) {
            return false;
        }
        AL10.alListener3f(AL10.AL_POSITION, posX, posY, posZ);
        AL10.alListener3f(AL10.AL_ORIENTATION, rotX, rotY, rotZ);
        AL10.alListener3f(AL10.AL_VELOCITY, velX, velY, velZ);
        return true;
    }

    /**
     * Sets the Component which blocks the setListenerData function
     *
     * @param component Component Active component or null
     * @param newComponent Component New component or null
     * @return <tt>true</tt> if the component was set successfully
     */
    public static final boolean setBlockingComponent(Component<?> component, Component<?> newComponent) {
        if (blockingAudioListenerComponent != null
                && (component == null || blockingAudioListenerComponent != component)) {
            return false;
        }
        blockingAudioListenerComponent = newComponent;
        return true;
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
                throw new UnsupportedAudioFileException("Can not handle Big Endian formats yet"); //TODO Implement Big Endian formats
            }
            final int openALFormat = AudioUtil.audioFormatToOpenALFormat(audioFormat);
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

    /**
     * Loads a sound from an InputStream to the static Soundbuffer
     *
     * @param name String Name of the Sound
     * @param inputStream InputStream Stream where should be read from
     * @return Integer BufferID where the sound was saved
     */
    @Deprecated
    public static final int loadSoundOLD(String name, InputStream inputStream) {
        deleteSound(name);
        final int bufferID = AL10.alGenBuffers();
        //final WaveData waveData = WaveData.create(inputStream);
        //AL10.alBufferData(bufferID, waveData.format, waveData.data, waveData.samplerate);
        //waveData.dispose();
        final Sound sound = new Sound(name, bufferID);
        sounds.add(sound);
        return bufferID;
    }

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

    /**
     * Deletes a Sound given by the name
     *
     * @param name String Name of the Sound to be deleted
     * @return <tt>true</tt> if the Sound was found and deleted
     */
    public static final boolean deleteSound(String name) {
        final Sound sound = getSound(name);
        if (sound != null) {
            boolean deleted = sound.delete(null);
            if (deleted) {
                sounds.remove(sound);
            }
            return deleted;
        } else {
            return false;
        }
    }

    /**
     * Deletes a Sound given by the bufferID
     *
     * @param bufferID Integer BufferID
     * @return <tt>true</tt> if the Sound was found and deleted
     */
    public static final boolean deleteSound(int bufferID) {
        final Sound sound = getSound(bufferID);
        if (sound != null) {
            boolean deleted = sound.delete(null);
            if (deleted) {
                sounds.remove(sound);
            }
            return deleted;
        } else {
            return false;
        }
    }

    /**
     * Cleans up every Sound and destroys the OpenAL AudioSystem
     */
    public static final void cleanup() {
        for (AudioSource source : AudioSource.audioSources) {
            source.delete();
        }
        AudioSource.audioSources.clear();
        for (Sound sound : sounds) {
            sound.delete(null);
        }
        sounds.clear();
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

    /**
     * Returns if the OpenAL AudioSystem is initialized
     *
     * @return <tt>true</tt> if the AudioSystem is initialized
     */
    public static final boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Returns the used DistanceModel
     *
     * @return DistanceModel DistanceModle used to calculate the volume of the
     * AudioSources
     */
    public static final DistanceModel getDistanceModel() {
        return distanceModel;
    }

    /**
     * Sets the used DistanceModel
     *
     * @param distanceModel DistanceModel DistanceModul that should be used to
     * calculate the volume of the AudioSources
     */
    public static final void setDistanceModel(DistanceModel distanceModel) {
        if (distanceModel == null) {
            return;
        }
        AL10.alDistanceModel(distanceModel.getDistanceModel());
        AudioManager.distanceModel = distanceModel;
    }

    /**
     * Distance model which calculates the roll off of the volume
     */
    public static enum DistanceModel {
        EXPONENT(AL11.AL_EXPONENT_DISTANCE, false),
        EXPONENT_CLAMPED(AL11.AL_EXPONENT_DISTANCE_CLAMPED, true),
        INVERSE(AL10.AL_INVERSE_DISTANCE, false),
        INVERSE_CLAMPED(AL10.AL_INVERSE_DISTANCE_CLAMPED, true),
        LINEAR(AL11.AL_LINEAR_DISTANCE, false),
        LINEAR_CLAMPED(AL11.AL_LINEAR_DISTANCE_CLAMPED, true);

        private final int distanceModel;
        private final boolean clamped;

        /**
         * Creates the DistanceModel
         *
         * @param distanceModel Integer OpenAL DistanceModel
         * @param clamped Boolean <tt>true</tt> if it is clamped
         */
        DistanceModel(int distanceModel, boolean clamped) {
            this.distanceModel = distanceModel;
            this.clamped = clamped;
        }

        /**
         * Returns the OpenAL-Equivalent for the DistanceModel
         *
         * @return Integer OpenAL Distancemodel
         */
        public final int getDistanceModel() {
            return distanceModel;
        }

        /**
         * Returns if the DistanceModel is a clamped one
         *
         * @return <tt>true</tt> if it is clamped
         */
        public final boolean isClamped() {
            return clamped;
        }

        public final float getFade(float fadeTime, float fadeTimeComplete, float volumeStart, float volumeTarget) {
            float newVolume = 0;
            switch (this) {
                case EXPONENT:
                case EXPONENT_CLAMPED:
                case INVERSE:
                case INVERSE_CLAMPED:
                    newVolume = (1.0F / (fadeTime + (1 / volumeStart))) + volumeTarget - (1.0F / (fadeTimeComplete + (1.0F / volumeStart)));
                    break;
                case LINEAR:
                case LINEAR_CLAMPED:
                    newVolume = fadeTime * ((volumeTarget - volumeStart) / fadeTimeComplete) + volumeStart;
                    break;

            }
            return Math.max(newVolume, 0.0F);
        }
    }

}
