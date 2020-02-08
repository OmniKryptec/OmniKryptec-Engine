package de.omnikryptec.resource.helper;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.libapi.openal.Sound;
import de.omnikryptec.libapi.openal.SoundLoader;
import de.omnikryptec.libapi.openal.StreamedSound;
import de.omnikryptec.resource.loadervpc.ResourceProvider;
import de.omnikryptec.resource.loadervpc.SoundFileWrapper;
import de.omnikryptec.util.Logger;

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
        Sound s = sounds.get(name);
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
        StreamedSound s = streamedSounds.get(key);
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
        streamedSounds.clear();
    }
    
    public void clearCachedSounds() {
        sounds.clear();
    }
    
    public void clearAndDeleteStreamedSounds() {
        for (StreamedSound s : streamedSounds.values()) {
            s.deleteAndUnregister();
        }
        clearStreamedSounds();
    }
    
    public void clearAndDeleteCachedSounds() {
        for (Sound s : sounds.values()) {
            s.deleteAndUnregister();
        }
        clearCachedSounds();
    }
}
