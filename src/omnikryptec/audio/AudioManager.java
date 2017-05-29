package omnikryptec.audio;

import omnikryptec.component.AudioListenerComponent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import omnikryptec.logger.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Panzer1119
 */
public class AudioManager {
    
    private static final HashMap<String, Integer> sounds = new HashMap<>();
    private static DistanceModel distanceModel = null;
    private static boolean isInitialized = false;
    private static AudioListenerComponent blockingAudioListenerComponent = null;
    
    public static final boolean init() {
        if(isInitialized) {
            return false;
        }
        try {
            AL.create();
            setDistanceModel(DistanceModel.EXPONENT_CLAMPED);
            isInitialized = true;
            return true;
        } catch (Exception ex) {
            isInitialized = false;
            Logger.logErr("Error while initializing the sound library: " + ex, ex);
            return false;
        }
    }
    
    public static final boolean setListenerData(AudioListenerComponent component, javax.vecmath.Vector3f position, javax.vecmath.Vector3f velocity) {
        return setListenerData(component, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }
    
    public static final boolean setListenerData(AudioListenerComponent component, Vector3f position, Vector3f velocity) {
        return setListenerData(component, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }
    
    public static final boolean setListenerData(AudioListenerComponent component, float posX, float posY, float posZ, float velX, float velY, float velZ) {
        if(blockingAudioListenerComponent != null && (component == null || blockingAudioListenerComponent != component)) {
            return false;
        }
        AL10.alListener3f(AL10.AL_POSITION, posX, posY, posZ);
        AL10.alListener3f(AL10.AL_VELOCITY, velX, velY, velZ);
        return true;
    }
    
    public static final boolean setBlockingAudioListenerComponent(AudioListenerComponent component, AudioListenerComponent newComponent) {
        if(blockingAudioListenerComponent != null && (component == null || blockingAudioListenerComponent != component)) {
            return false;
        }
        blockingAudioListenerComponent = newComponent;
        return true;
    }
    
    public static final int loadSound(String name, File file) throws FileNotFoundException {
        return loadSound(name, new FileInputStream(file));
    }
    
    public static final int loadSound(String name, String path) {
        return loadSound(name, AudioManager.class.getResourceAsStream(path));
    }
    
    public static final int loadSound(String name, InputStream inputStream) {
        deleteSound(name);
        final int bufferID = AL10.alGenBuffers();
        sounds.put(name, bufferID);
        final WaveData waveData = WaveData.create(inputStream);
        AL10.alBufferData(bufferID, waveData.format, waveData.data, waveData.samplerate);
        waveData.dispose();
        return bufferID;
    }
    
    public static final Integer getSound(String name) {
        return sounds.get(name);
    }
    
    public static final String[] getSoundNames() {
        return sounds.keySet().toArray(new String[sounds.size()]);
    }
    
    public static final String getSoundName(Integer bufferID) {
        if(bufferID == null) {
            return null;
        }
        for(String name : sounds.keySet()) {
            if(sounds.get(name).equals(bufferID)) {
                return name;
            }
        }
        return null;
    }
    
    public static final boolean deleteSound(String name) {
        final Integer bufferID = sounds.get(name);
        if(bufferID != null) {
            try {
                AL10.alDeleteBuffers(bufferID);
                sounds.remove(name);
                return true;
            } catch (Exception ex) {
                if(Logger.isDebugMode()) {
                    Logger.logErr("Error while deleting existing sound: " + ex, ex);
                }
                return false;
            }
        } else {
            return false;
        }
    }
    
    public static final void cleanup() {
        for(int bufferID : sounds.values()) {
            AL10.alDeleteBuffers(bufferID);
        }
        sounds.clear();
        AL.destroy();
    }
    
    public static final boolean isInitialized() {
        return isInitialized;
    }
    
    public static final DistanceModel getDistanceModel() {
        return distanceModel;
    }
    
    public static final void setDistanceModel(DistanceModel distanceModel) {
        if(distanceModel == null) {
            return;
        }
        AL10.alDistanceModel(distanceModel.getDistanceModel());
        AudioManager.distanceModel = distanceModel;
    }
    
    public static enum DistanceModel {
        EXPONENT            (AL11.AL_EXPONENT_DISTANCE,         false),
        EXPONENT_CLAMPED    (AL11.AL_EXPONENT_DISTANCE_CLAMPED, true),
        INVERSE             (AL10.AL_INVERSE_DISTANCE,          false),
        INVERSE_CLAMPED     (AL10.AL_INVERSE_DISTANCE_CLAMPED,  true),
        LINEAR              (AL11.AL_LINEAR_DISTANCE,           false),
        LINEAR_CLAMPED      (AL11.AL_LINEAR_DISTANCE_CLAMPED,   true);
        
        private final int distanceModel;
        private final boolean clamped;
        
        DistanceModel(int distanceModel, boolean clamped) {
            this.distanceModel = distanceModel;
            this.clamped = clamped;
        }
        
        public final int getDistanceModel() {
            return distanceModel;
        }
        
        public final boolean isClamped() {
            return clamped;
        }
    }
    
}
