package de.omnikryptec.libapi.openal;

import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import de.omnikryptec.util.Util;

public class OpenAL {

    private static boolean created = false;
    
    private DistanceModel distanceModel;
    
    private long defaultDevice;
    private long context;
    private ALCCapabilities deviceCaps;
    
    public void shutdown() {
        ALC10.alcMakeContextCurrent(0);
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(defaultDevice);
    }
    
    public OpenAL() {
        if(created) {
            throw new IllegalStateException("OpenAL has already been created");
        }
        defaultDevice = ALC10.alcOpenDevice((ByteBuffer)null);
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
        if(!ALC10.alcMakeContextCurrent(context)) {
            throw new RuntimeException("Failed to make OpenAL context current!");
        }
        AL.createCapabilities(deviceCaps);
        
    }
    
    public DistanceModel getDistanceModel() {
        return distanceModel;
    }
    
    public void setDistanceModel(DistanceModel distanceModel) {
        AL10.alDistanceModel(Util.ensureNonNull(distanceModel).getDistanceModelId());
        this.distanceModel = distanceModel;
    }
}
