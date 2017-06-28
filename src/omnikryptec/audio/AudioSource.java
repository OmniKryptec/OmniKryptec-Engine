package omnikryptec.audio;

import java.util.ArrayList;
import org.joml.Vector3f;

import org.lwjgl.openal.AL10;

/**
 * Source which plays the sounds
 *
 * @author Panzer1119
 */
public class AudioSource {

    protected static final ArrayList<AudioSource> audioSources = new ArrayList<>();

    private final int sourceID;
    private float pitch = 1.0F;
    private float deltaPitch = 0.0F;
    private boolean pause = false;
    private boolean affectedByPhysics = false;
    private ISound sound = null;

    /**
     * Creates an empty AudioSource
     */
    public AudioSource() {
        sourceID = AL10.alGenSources();
        setVolume(1.0F);
        setPitch(1.0F);
        setPosition(0, 0, 0);
        audioSources.add(this);
    }

    /**
     * Plays and sets the Sound given by the name
     *
     * @param name String Name of the Sound to be played
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource play(String name) {
        return play(AudioManager.getSound(name));
    }

    /**
     * Plays and sets the ISound
     *
     * @param sound ISound Sound to be played
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource play(ISound sound) {
        stop();
        if (sound == null) {
            return this;
        }
        sound.play(this);
        this.sound = sound;
        continuePlaying();
        return this;
    }

    /**
     * Returns the set ISound
     *
     * @return ISound Set ISound
     */
    public final ISound getSound() {
        return sound;
    }

    /**
     * Sets if the ISound should be played in a loop
     *
     * @param loop Boolean <tt>true</tt> if the ISound should be played in a
     * loop
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setLooping(boolean loop) {
        AL10.alSourcei(sourceID, AL10.AL_LOOPING, (loop ? AL10.AL_TRUE : AL10.AL_FALSE));
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
            sound.stop(this);
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
     * Deletes the AudioSource and stops every ISound on this Object
     *
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource delete() {
        stop();
        AL10.alDeleteSources(sourceID);
        if (sound != null) {
            sound.delete(this);
        }
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
     * Sets the orientation
     *
     * @param orientation Vector3f Vector of the orientation
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setOrientation(javax.vecmath.Vector3f orientation) {
        setOrientation(orientation.x, orientation.y, orientation.z);
        return this;
    }

    /**
     * Sets the orientation
     *
     * @param orientation Vector3f Vector of the orientation
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setOrientation(Vector3f orientation) {
        setOrientation(orientation.x, orientation.y, orientation.z);
        return this;
    }

    /**
     * Sets the orientation
     *
     * @param x Float Float of the x-orientation
     * @param y Float Float of the y-orientation
     * @param z Float Float of the z-orientation
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setOrientation(float x, float y, float z) {
        AL10.alSource3f(sourceID, AL10.AL_ORIENTATION, x, y, z);
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
     * @param referenceDistance Float Reference distance (Reference distance is
     * the point where the Volume = 1.0F)
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
     * Returns if this AudioSource is affected by the physics speed
     *
     * @return <tt>true</tt> if this AudioSource is affected by the physics
     * speed
     */
    public final boolean isAffectedByPhysics() {
        return affectedByPhysics;
    }

    /**
     * Sets if this AudioSource is affected by the physics speed
     *
     * @param affectedByPhysics Boolean If this AudioSource is effected by the
     * physics speed
     * @return AudioSource A reference to this AudioSource
     */
    public final AudioSource setAffectedByPhysics(boolean affectedByPhysics) {
        this.affectedByPhysics = affectedByPhysics;
        return this;
    }

    protected final int getSourceID() {
        return sourceID;
    }

}
