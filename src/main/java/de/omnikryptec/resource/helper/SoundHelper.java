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

package de.omnikryptec.resource.helper;

import de.omnikryptec.libapi.openal.Sound;
import de.omnikryptec.libapi.openal.SoundLoader;
import de.omnikryptec.libapi.openal.StreamedSound;
import de.omnikryptec.resource.loadervpc.ResourceProvider;
import de.omnikryptec.resource.loadervpc.SoundFileWrapper;
import de.omnikryptec.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class SoundHelper {

    private static final Logger LOGGER = Logger.getLogger(SoundHelper.class);

    private final Map<String, Sound> sounds;
    private final Map<String, StreamedSound> streamedSounds;
    private final ResourceProvider resProvider;

    public SoundHelper(ResourceProvider prov) {
        this.resProvider = prov;
        this.sounds = new HashMap<>();
        this.streamedSounds = new HashMap<>();
    }

    public Sound getCached(String name) {
        Sound s = this.sounds.get(name);
        if (s == null) {
            SoundFileWrapper soundFileWrapper = this.resProvider.get(SoundFileWrapper.class, name);
            if (soundFileWrapper == null) {
                LOGGER.error(String.format("Could not find the Soundfile \"%s\"", name));
                return null;
            }
            s = SoundLoader.loadSound(soundFileWrapper.soundFile);
            this.sounds.put(name, s);
        }
        return s;
    }

    public StreamedSound getStreamed(String name) {
        return getStreamed(name, 0);
    }

    //A StreamedSound can not be used in more than one AudioSource at the same time.
    //Different numbers create new StreamedSounds but from the same Audiofile so they can be used in parallel.
    public StreamedSound getStreamed(String name, int number) {
        String key = name + number;
        StreamedSound s = this.streamedSounds.get(key);
        if (s == null) {
            SoundFileWrapper soundFileWrapper = this.resProvider.get(SoundFileWrapper.class, name);
            if (soundFileWrapper == null) {
                LOGGER.error(String.format("Could not find the Soundfile \"%s\"", name));
                return null;
            }
            s = SoundLoader.streamSound(soundFileWrapper.soundFile);
            this.streamedSounds.put(key, s);
        }
        return s;
    }

    public void clearStreamedSounds() {
        this.streamedSounds.clear();
    }

    public void clearCachedSounds() {
        this.sounds.clear();
    }

    public void clearAndDeleteStreamedSounds() {
        for (StreamedSound s : this.streamedSounds.values()) {
            s.deleteAndUnregister();
        }
        clearStreamedSounds();
    }

    public void clearAndDeleteCachedSounds() {
        for (Sound s : this.sounds.values()) {
            s.deleteAndUnregister();
        }
        clearCachedSounds();
    }
}
