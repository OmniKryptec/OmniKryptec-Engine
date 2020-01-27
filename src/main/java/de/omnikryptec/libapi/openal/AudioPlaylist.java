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

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Timer;

import de.omnikryptec.util.Logger;

/**
 * AudioPlaylist
 *
 * @author Panzer1119
 */
public class AudioPlaylist {
    
    private static final Logger LOGGER = Logger.getLogger(AudioPlaylist.class);
    
    private final ArrayList<ALSound> sounds = new ArrayList<>();
    private final AudioSource source_1 = new AudioSource();
    private final AudioSource source_2 = new AudioSource();
    private boolean playerIsSource1 = false;
    private float fadingStart = 5.0F;
    private double playStarted = 0.0;
    private int pointer = -1;
    private boolean isFading = false;
    private boolean isPlaying = false;
    private boolean loop = false;
    
    // TODO Panzer1119 Vielleicht das Playlist extenden lassen, und dann ist PlayList eine
    // lustige PlayList, die alle funktionen generisch eingebaut hat wie shuffle
    // oder einfach abspielen mit naechster und so
    public AudioPlaylist(ALSound... sounds) {
        addSounds(sounds);
        source_1.setFadeTimeComplete(5.0F);
        source_2.setFadeTimeComplete(5.0F);
    }
    
    public final boolean addSounds(ALSound... sounds) {
        if (sounds != null && sounds.length > 0) {
            this.sounds.addAll(Arrays.asList(sounds));
            return true;
        } else {
            return false;
        }
    }
    
    public final boolean removeSounds(ALSound... sounds) { // TODO Panzer1119 Falls der sound gerade gespielt wird, muss er gestoppt
                                                           // werden
        if (sounds != null && sounds.length > 0) {
            for (ALSound sound : sounds) {
                this.sounds.remove(sound);
            }
            checkPointer();
            return true;
        } else {
            return false;
        }
    }
    
    public final ALSound[] getSounds() {
        return sounds.toArray(new ALSound[sounds.size()]);
    }
    
    private final AudioSource getPlayingAudioSource() {
        return (playerIsSource1 ? source_1 : source_2);
    }
    
    private final AudioSource getWaitingAudioSource() {
        return (playerIsSource1 ? source_2 : source_1);
    }
    
    private final AudioPlaylist play(ALSound sound) {
        if (sound == null) {
            stop();
        }
        LOGGER.info("Playing: " + sound);
        playStarted = System.currentTimeMillis();
        getWaitingAudioSource().stop();
        getWaitingAudioSource().setVolume(1.0F);
        getWaitingAudioSource().setEffectState(AudioEffectState.FADE_IN);
        getWaitingAudioSource().play(sound);
        playerIsSource1 = !playerIsSource1;
        OpenAL.ACTIVE_PLAYLISTS.add(this);
        return this;
    }
    
    private final ALSound next() {
        if (sounds.isEmpty() || pointer == -1) {
            return null;
        }
        final ALSound sound = sounds.get(pointer);
        pointer++;
        if (loop) {
            checkPointer();
        } else if (pointer >= sounds.size()) {
            pointer = -1;
        }
        return sound;
    }
    
    private final AudioPlaylist checkPointer() {
        while (pointer < 0) {
            pointer += sounds.size();
        }
        pointer = pointer % sounds.size();
        return this;
    }
    
    public final AudioPlaylist start() {
        pointer = 0;
        isPlaying = true;
        play(next());
        return this;
    }
    
    public final AudioPlaylist pause() {
        isPlaying = false;
        getPlayingAudioSource().pause();
        return this;
    }
    
    public final AudioPlaylist continuePlaying() {
        isPlaying = true;
        getPlayingAudioSource().continuePlaying();
        return this;
    }
    
    public final AudioPlaylist stop() {
        pointer = 0;
        isPlaying = false;
        source_1.stop();
        source_2.stop();
        OpenAL.ACTIVE_PLAYLISTS.remove(this);
        return this;
    }
    
    public final AudioPlaylist shuffle() {
        pointer = 0;
        final int shuffleTimes = Math.max((int) (Math.random() * 8.0 + 2.0), 2);
        for (int i = 0; i < shuffleTimes; i++) {
            sounds.sort((sound_1, sound_2) -> (int) (Math.random() * 3 - 1.5));
        }
        LOGGER.debug("Shuffled AudioPlaylist " + shuffleTimes + " times");
        return this;
    }
    
    /**
     * Returns how many seconds before a track ends its fading out
     *
     * @return Fading start
     */
    public final float getFadingStart() {
        return fadingStart;
    }
    
    public final AudioPlaylist setFadingStart(float fadingStart) {
        this.fadingStart = fadingStart;
        return this;
    }
    
    public final boolean isLoop() {
        return loop;
    }
    
    public final AudioPlaylist setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }
    
    private final AudioPlaylist startFading() {
        if (isFading) {
            return this;
        }
        isFading = true;
        getPlayingAudioSource().setEffectState(AudioEffectState.FADE_OUT);
        play(next());
        isFading = false;
        return this;
    }
    
    final AudioPlaylist update() {
        //FIXME Panzer1119 length of streamed sounds
        //        if (!isPlaying) {
        //            return this;
        //        }
        //        final double deltaTime = (System.currentTimeMillis() - playStarted) / 1000.0;
        //        LOGGER.debug("Length: " + getPlayingAudioSource().getSound().getLength());
        //        if (deltaTime >= getPlayingAudioSource().getSound().getLength() - fadingStart) {
        //            startFading();
        //        }
        //        source_1.updateState(timer.getDelay() / 1000.0F);
        //        source_2.updateState(timer.getDelay() / 1000.0F);
        return this;
    }
}
