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

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.Util;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import javax.swing.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

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

    private final long defaultDevice;
    private final long context;
    private final ALCCapabilities deviceCaps;

    /**
     * Used internally by the engine. Instead, use the LibAPIManager
     */
    public void shutdown() {
        ALC10.alcMakeContextCurrent(0);
        ALC10.alcDestroyContext(this.context);
        ALC10.alcCloseDevice(this.defaultDevice);
    }

    public OpenAL() {
        if (created) {
            throw new IllegalStateException("OpenAL has already been created");
        }
        this.defaultDevice = ALC10.alcOpenDevice("OpenAL Soft"); //Hardware accelerated OpenAL is apparently dead and also the performance of the Software renderer is better
        if (this.defaultDevice == 0) {
            throw new RuntimeException("No audio device has been found");
        }
        this.deviceCaps = ALC.createCapabilities(this.defaultDevice);
        IntBuffer contextAttributeList = BufferUtils.createIntBuffer(16);
        contextAttributeList.put(ALC10.ALC_REFRESH);
        contextAttributeList.put(60);
        contextAttributeList.put(ALC10.ALC_SYNC);
        contextAttributeList.put(ALC10.ALC_FALSE);
        contextAttributeList.put(ALC_MAX_AUXILIARY_SENDS);
        contextAttributeList.put(2);
        contextAttributeList.put(0);
        contextAttributeList.flip();
        this.context = ALC10.alcCreateContext(this.defaultDevice, contextAttributeList);
        if (!ALC10.alcMakeContextCurrent(this.context)) {
            throw new RuntimeException("Failed to make OpenAL context current!");
        }
        AL.createCapabilities(this.deviceCaps);
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
        AL10.alDopplerFactor(f);
    }

    public DistanceModel getDistanceModel() {
        return this.distanceModel;
    }

    public void setDistanceModel(DistanceModel distanceModel) {
        AL10.alDistanceModel(Util.ensureNonNull(distanceModel).getDistanceModelId());
        this.distanceModel = distanceModel;
    }
}
