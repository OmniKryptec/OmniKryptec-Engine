package omnikryptec.audio;

import java.util.ArrayList;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Panzer1119
 */
public class AudioSource {
    
    protected static final ArrayList<AudioSource> audioSources = new ArrayList<>();
    
    private final int sourceID;
    private ISound sound = null;
    
    public AudioSource() {
        sourceID = AL10.alGenSources();
        setVolume(1.0F);
        setPitch(1.0F);
        setPosition(0, 0, 0);
        audioSources.add(this);
    }
    
    public final AudioSource play(String name) {
        return play(AudioManager.getSound(name));
    }
    
    public final AudioSource play(ISound sound) {
        stop();
        if(sound == null) {
            return this;
        }
        sound.play(this);
        this.sound = sound;
        continuePlaying();
        return this;
    }
    
    public final ISound getSound() {
        return sound;
    }
    
    public final AudioSource setLooping(boolean loop) {
        AL10.alSourcei(sourceID, AL10.AL_LOOPING, (loop ? AL10.AL_TRUE : AL10.AL_FALSE));
        return this;
    }
    
    public final boolean isPlaying() {
        return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }
    
    public final AudioSource pause() {
        AL10.alSourcePause(sourceID);
        return this;
    }
    
    public final AudioSource continuePlaying() {
        AL10.alSourcePlay(sourceID);
        return this;
    }
    
    public final AudioSource stop() {
        AL10.alSourceStop(sourceID);
        if(sound != null) {
            sound.stop(this);
        }
        return this;
    }
    
    public final AudioSource restart() {
        stop();
        continuePlaying();
        return this;
    }
    
    public final AudioSource delete() {
        stop();
        AL10.alDeleteSources(sourceID);
        if(sound != null) {
            sound.delete(this);
        }
        return this;
    }
    
    public final AudioSource setVolume(float volume) {
        AL10.alSourcef(sourceID, AL10.AL_GAIN, volume);
        return this;
    }
    
    public final float getVolume() {
        return AL10.alGetSourcef(sourceID, AL10.AL_GAIN);
    }
    
    public final AudioSource setPitch(float pitch) {
        AL10.alSourcef(sourceID, AL10.AL_PITCH, pitch);
        return this;
    }
    
    public final float getPitch() {
        return AL10.alGetSourcef(sourceID, AL10.AL_PITCH);
    }
    
    public final AudioSource setPosition(javax.vecmath.Vector3f position) {
        setPosition(position.x, position.y, position.z);
        return this;
    }
    
    public final AudioSource setPosition(Vector3f position) {
        setPosition(position.x, position.y, position.z);
        return this;
    }
    
    public final AudioSource setPosition(float x, float y, float z) {
        AL10.alSource3f(sourceID, AL10.AL_POSITION, x, y, z);
        return this;
    }
    
    public final AudioSource setRotation(javax.vecmath.Vector3f rotation) {
        setRotation(rotation.x, rotation.y, rotation.z);
        return this;
    }
    
    public final AudioSource setRotation(Vector3f rotation) {
        setRotation(rotation.x, rotation.y, rotation.z);
        return this;
    }
    
    public final AudioSource setRotation(float x, float y, float z) {
        AL10.alSource3f(sourceID, AL10.AL_ORIENTATION, x, y, z);
        return this;
    }
    
    public final AudioSource setVelocity(javax.vecmath.Vector3f velocity) {
        setVelocity(velocity.x, velocity.y, velocity.z);
        return this;
    }
    
    public final AudioSource setVelocity(Vector3f velocity) {
        setVelocity(velocity.x, velocity.y, velocity.z);
        return this;
    }
    
    public final AudioSource setVelocity(float x, float y, float z) {
        AL10.alSource3f(sourceID, AL10.AL_VELOCITY, x, y, z);
        return this;
    }
    
    public final AudioSource setRollOffFactor(float rollOffFactor) {
        AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, rollOffFactor);
        return this;
    }
    
    public final AudioSource setReferenceDistance(float referenceDistance) {
        AL10.alSourcef(sourceID, AL10.AL_REFERENCE_DISTANCE, referenceDistance);
        return this;
    }
    
    public final AudioSource setMaxDistance(float maxDistance) {
        AL10.alSourcef(sourceID, AL10.AL_MAX_DISTANCE, maxDistance);
        return this;
    }
    
    protected final int getSourceID() {
        return sourceID;
    }
    
}
