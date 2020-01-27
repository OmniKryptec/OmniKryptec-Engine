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

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.exposed.LibAPIManager;

/**
 * Source which plays the sounds
 *
 * @author Panzer1119
 */
public class AudioSource implements Deletable {
    
    //protected static final ArrayList<AudioSource> audioSources = new ArrayList<>();
    
    private final int sourceID;
    private float pitch = 1.0F;
    private float deltaPitch = 0.0F;
    private boolean pause = false;
    private AudioEffectState effectState = AudioEffectState.NOTHING;
    private float fadeTimeComplete = 1000.0F;
    private float fadeTime = 0.0F;
    private float volumeStart = 0.0F;
    private float volumeTarget = 1.0F;
    
    private ALSound sound = null;
    
    //TODO pcfreak9000 add direction + sound cone properties
    
    /**
     * Creates an empty AudioSource
     */
    public AudioSource() {
        sourceID = AL10.alGenSources();
        registerThisAsAutodeletable();
        setVolume(1.0F);
        setPitch(1.0F);
        setPosition(0, 0, 0);
        //audioSources.add(this);
    }
    
    /**
     * Fades one frame
     *
     * @param deltaTime out If fading out or fading in
     * @return <tt>true</tt> if next frame also needs to be faded again
     */
    public final boolean fadeOneFrame(float deltaTime) {
        final boolean out = (Math.max(volumeStart, volumeTarget) == volumeStart);
        float newVolume = LibAPIManager.instance().getOpenAL().getDistanceModel().getFade(fadeTime,
                fadeTimeComplete / 1000.0F, Math.max(volumeStart, volumeTarget), Math.min(volumeStart, volumeTarget));
        fadeTime += (deltaTime * (out ? 1.0F : -1.0F));
        setVolume(newVolume);
        if (out) {
            return (getVolume() - volumeTarget) > 0.0F;
        } else {
            return (volumeTarget - getVolume()) > 0.0F;
        }
    }
    
    /**
     * Sets the AudioEffectState
     *
     * @param effectState AudioEffectState
     * @return A reference to this AudioSource
     */
    public final AudioSource setEffectState(AudioEffectState effectState) {
        if (this.effectState != effectState) {
            switch (effectState) {
            case NOTHING:
                break;
            case FADE_IN:
                volumeStart = 0.0F;
                volumeTarget = getVolume();
                if (volumeTarget <= 0.0F) {
                    volumeTarget = 1.0F;
                }
                fadeTime = fadeTimeComplete / 1000.0F;
                break;
            case FADE_OUT:
                volumeStart = getVolume();
                volumeTarget = 0.0F;
                fadeTime = 0.0F;
                break;
            }
        }
        this.effectState = effectState;
        return this;
    }
    
    /**
     * Updates the AudioEffectState
     *
     * @return A reference to this AudioSource
     */
    public final AudioSource updateState(float deltaTime) {
        switch (effectState) {
        case NOTHING:
            break;
        case FADE_IN:
            if (!fadeOneFrame(deltaTime)) {
                effectState = AudioEffectState.NOTHING;
                setVolume(volumeTarget);
            }
            break;
        case FADE_OUT:
            if (!fadeOneFrame(deltaTime)) {
                effectState = AudioEffectState.NOTHING;
                setVolume(volumeTarget);
            }
            break;
        
        }
        return this;
    }
    
    /**
     * Plays and sets the ISound
     *
     * @param sound ISound Sound to be played
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource play(ALSound sound) {
        stop();
        if (sound == null) {
            return this;
        }
        sound.attach(this);
        this.sound = sound;
        continuePlaying();
        return this;
    }
    
    /**
     * Returns the set ISound
     *
     * @return ISound Set ISound
     */
    public final ALSound getSound() {
        return sound;
    }
    
    /**
     * Sets if the ISound should be played in a loop
     *
     * @param loop Boolean <tt>true</tt> if the ISound should be played in a loop
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setLooping(boolean loop) {
        AL10.alSourcei(sourceID, AL10.AL_LOOPING, OpenALUtil.booleanToOpenAL(loop));
        return this;
    }
    
    /**
     * Returns if any ISound is actually played
     *
     * @return <tt>true</tt> if the AudioSource is playing
     */
    public final boolean isPlaying() {
        return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }
    
    /**
     * Pauses the AudioSource
     *
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource pause() {
        pause = true;
        pauseTemporarily();
        return this;
    }
    
    /**
     * Pauses the AudioSource temporarily
     *
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource pauseTemporarily() {
        AL10.alSourcePause(sourceID);
        return this;
    }
    
    /**
     * Continues the ISound playing
     *
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource continuePlaying() {
        AL10.alSourcePlay(sourceID);
        pause = false;
        return this;
    }
    
    /**
     * Continues the ISound playing temporarily
     *
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource continuePlayingTemporarily() {
        if (!pause && !isPlaying()) {
            continuePlaying();
        }
        return this;
    }
    
    /**
     * Stops the AudioSources
     *
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource stop() {
        pause = true;
        AL10.alSourceStop(sourceID);
        if (sound != null) {
            sound.detach();
        }
        return this;
    }
    
    /**
     * Restarts the AudioSource
     *
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource restart() {
        stop();
        continuePlaying();
        return this;
    }
    
    /**
     * Sets the volume
     *
     * @param volume Float Volume (0.0F = Nothing, 1.0F = Normal)
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setVolume(float volume) {
        AL10.alSourcef(sourceID, AL10.AL_GAIN, volume);
        return this;
    }
    
    /**
     * Returns the volume
     *
     * @return Float Volume
     */
    public final float getVolume() {
        return AL10.alGetSourcef(sourceID, AL10.AL_GAIN);
    }
    
    /**
     * Sets the real pitch
     *
     * @return AudioSource A reference to this AudioSource
     */
    protected final AudioSource setRealPitch() {
        AL10.alSourcef(sourceID, AL10.AL_PITCH, pitch + deltaPitch);
        return this;
    }
    
    /**
     * Returns the real pitch
     *
     * @return Float Real pitch
     */
    public final float getRealPitch() {
        return AL10.alGetSourcef(sourceID, AL10.AL_PITCH);
    }
    
    /**
     * Sets the pitch
     *
     * @param pitch Float Pitch (1.0F = Normal)
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setPitch(float pitch) {
        this.pitch = pitch;
        setRealPitch();
        return this;
    }
    
    /**
     * Returns the setted pitch
     *
     * @return Float Setted pitch
     */
    public final float getPitch() {
        return pitch;
    }
    
    /**
     * Sets the delta which gets added to the pitch
     *
     * @param deltaPitch Float Pitch delta (0.0F = Normal)
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setDeltaPitch(float deltaPitch) {
        this.deltaPitch = deltaPitch;
        setRealPitch();
        return this;
    }
    
    /**
     * Returns the delta which gets added to the pitch
     *
     * @return Float Pitch delta
     */
    public final float getDeltaPitch() {
        return deltaPitch;
    }
    
    /**
     * Sets the position
     *
     * @param position Vector3f Vector of the position
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setPosition(javax.vecmath.Vector3f position) {
        setPosition(position.x, position.y, position.z);
        return this;
    }
    
    /**
     * Sets the position
     *
     * @param position Vector3f Vector of the position
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setPosition(Vector3f position) {
        setPosition(position.x, position.y, position.z);
        return this;
    }
    
    /**
     * Sets the position
     *
     * @param x Float Float of the x-position
     * @param y Float Float of the y-position
     * @param z Float Float of the z-position
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setPosition(float x, float y, float z) {
        AL10.alSource3f(sourceID, AL10.AL_POSITION, x, y, z);
        return this;
    }
    
    /**
     * Sets the velocity
     *
     * @param velocity Vector3f Vector of the velocity
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setVelocity(javax.vecmath.Vector3f velocity) {
        setVelocity(velocity.x, velocity.y, velocity.z);
        return this;
    }
    
    /**
     * Sets the velocity
     *
     * @param velocity Vector3f Vector of the velocity
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setVelocity(Vector3f velocity) {
        setVelocity(velocity.x, velocity.y, velocity.z);
        return this;
    }
    
    /**
     * Sets the velocity
     *
     * @param x Float Float of the x-velocity
     * @param y Float Float of the y-velocity
     * @param z Float Float of the z-velocity
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setVelocity(float x, float y, float z) {
        AL10.alSource3f(sourceID, AL10.AL_VELOCITY, x, y, z);
        return this;
    }
    
    public final AudioSource setRelative(boolean b) {
        AL10.alSourcei(sourceID, AL11.AL_SOURCE_RELATIVE, OpenALUtil.booleanToOpenAL(b));
        return this;
    }
    
    /**
     * Sets how fast the volume decreases over distance
     *
     * @param rollOffFactor Float Roll-off-Factor
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setRollOffFactor(float rollOffFactor) {
        AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, rollOffFactor);
        return this;
    }
    
    /**
     * Sets the reference distance
     *
     * @param referenceDistance Float Reference distance (Reference distance is the
     *                          point where the Volume = 1.0F)
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setReferenceDistance(float referenceDistance) {
        AL10.alSourcef(sourceID, AL10.AL_REFERENCE_DISTANCE, referenceDistance);
        return this;
    }
    
    /**
     * Sets the maximum distance for clamped DistanceModels
     *
     * @param maxDistance Float Maximum distance
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setMaxDistance(float maxDistance) {
        AL10.alSourcef(sourceID, AL10.AL_MAX_DISTANCE, maxDistance);
        return this;
    }
    
    /**
     * Returns the AudioEffectState
     *
     * @return AudioEffectState
     */
    public final AudioEffectState getEffectState() {
        return effectState;
    }
    
    /**
     * Returns the Fade tim
     *
     * @return Fading in and out time
     */
    public final float getFadeTimeComplete() {
        return fadeTimeComplete;
    }
    
    /**
     * Sets the fading in and out time
     *
     * @param fadeTimeComplete Fade time in milliseconds
     * @return A reference to this AudioSource
     */
    public final AudioSource setFadeTimeComplete(float fadeTimeComplete) {
        this.fadeTimeComplete = fadeTimeComplete;
        return this;
    }
    
    protected final int getSourceID() {
        return sourceID;
    }
    
    @Override
    public void deleteRaw() {
        stop();
        AL10.alDeleteSources(this.sourceID);
    }
    
}
