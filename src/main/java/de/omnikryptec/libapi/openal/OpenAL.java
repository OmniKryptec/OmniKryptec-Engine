package de.omnikryptec.libapi.openal;

import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.Util;

public class OpenAL {
    
    private static boolean created = false;
    
    static final List<StreamedSound> ACTIVE_STREAMED_SOUNDS = new ArrayList<>();
    static final List<AudioPlaylist> ACTIVE_PLAYLISTS = new ArrayList<>();
    
    private static final Timer UPDATE_TIMER = new Timer(10, e -> {
        for (StreamedSound s : ACTIVE_STREAMED_SOUNDS) {
            s.update();
        }
        for (AudioPlaylist p : ACTIVE_PLAYLISTS) {
            p.update();
        }
    });
    
    static {
        LibAPIManager.registerResourceShutdownHooks(() -> UPDATE_TIMER.stop());
    }
    
    private DistanceModel distanceModel;
    
    private long defaultDevice;
    private long context;
    private ALCCapabilities deviceCaps;
    
    /**
     * Used internally by the engine. Instead, use the LibAPIManager
     */
    public void shutdown() {
        ALC10.alcMakeContextCurrent(0);
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(defaultDevice);
    }
    
    public OpenAL() {
        if (created) {
            throw new IllegalStateException("OpenAL has already been created");
        }
        //        List<String> l = ALUtil.getStringList(0, ALC11.ALC_ALL_DEVICES_SPECIFIER);
        //        for(String s : l) {
        //            System.out.println(s);
        //        }
        //        System.out.println(ALC11.alcGetString(0, ALC11.ALC_DEFAULT_DEVICE_SPECIFIER));
        //        System.out.println(ALC11.alcGetString(0, ALC11.ALC_DEVICE_SPECIFIER));
        //TODO pick a better device
        defaultDevice = ALC10.alcOpenDevice((ByteBuffer) null);
        if (defaultDevice == 0) {
            throw new RuntimeException("No audio device has been found");
        }
        deviceCaps = ALC.createCapabilities(defaultDevice);
        IntBuffer contextAttributeList = BufferUtils.createIntBuffer(16);
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
            throw new RuntimeException("Failed to make OpenAL context current!");
        }
        AL.createCapabilities(deviceCaps);
        UPDATE_TIMER.start();
        setDistanceModel(DistanceModel.NONE);
    }
    
    public void setMasterGain(float f) {
        AL10.alListenerf(AL10.AL_GAIN, f);
    }
    
    public void setListenerPosition(float x, float y, float z) {
        AL10.alListener3f(AL10.AL_POSITION, x, y, z);
    }
    
    public void setListenerVelocity(float vx, float vy, float vz) {
        AL10.alListener3f(AL10.AL_VELOCITY, vx, vy, vz);
    }
    
    public void setListenerOrientation(float atx, float aty, float atz, float upx, float upy, float upz) {
        AL10.alListenerfv(AL10.AL_ORIENTATION, new float[] { atx, aty, atz, upx, upy, upz });
    }
    
    public void setSpeedOfSound(float v) {
        AL11.alSpeedOfSound(v);
    }
    
    public void setDopplerFactor(float f) {
        AL11.alDopplerFactor(f);
    }
    
    public DistanceModel getDistanceModel() {
        return distanceModel;
    }
    
    public void setDistanceModel(DistanceModel distanceModel) {
        AL10.alDistanceModel(Util.ensureNonNull(distanceModel).getDistanceModelId());
        this.distanceModel = distanceModel;
    }
}
