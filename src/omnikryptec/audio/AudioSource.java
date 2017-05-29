package omnikryptec.audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Panzer1119
 */
public class AudioSource {
    
    private final int sourceID;
    private int playedBufferID = -1;
    
    public AudioSource() {
        sourceID = AL10.alGenSources();
        AL10.alSourcef(sourceID, AL10.AL_GAIN, 1);
        AL10.alSourcef(sourceID, AL10.AL_PITCH, 1);
        AL10.alSource3f(sourceID, AL10.AL_POSITION, 0, 0, 0);
    }
    
    public final boolean play(String name) {
        final Integer bufferID = AudioManager.getSound(name);
        if(bufferID != null) {
            play(bufferID);
            return true;
        } else {
            return false;
        }
    }
    
    public final AudioSource play(int bufferID) {
        stop();
        AL10.alSourcei(sourceID, AL10.AL_BUFFER, bufferID);
        this.playedBufferID = bufferID;
        continuePlaying();
        return this;
    }
    
    public final int getSound() {
        return playedBufferID;
    }
    
    public final String getSoundName() {
        return AudioManager.getSoundName(playedBufferID);
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
        return this;
    }
    
    public final AudioSource setVolume(float volume) {
        AL10.alSourcef(sourceID, AL10.AL_GAIN, volume);
        return this;
    }
    
    public final AudioSource setPitch(float pitch) {
        AL10.alSourcef(sourceID, AL10.AL_PITCH, pitch);
        return this;
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
    
}
