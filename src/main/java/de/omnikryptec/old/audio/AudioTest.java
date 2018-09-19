/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.audio;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.old.util.NativesLoader;
import de.omnikryptec.old.util.logger.Commands;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;
import org.joml.Vector3f;

import javax.swing.*;
import java.util.Arrays;

/**
 * Test class for the audio functions
 *
 * @author Panzer1119
 */
public class AudioTest {

    private static final Timer timer = new Timer(10, (e) -> {
        AudioManager.update(0);
    });

    public static void main(String[] args) {
        NativesLoader.loadNatives();
        Logger.setDebugMode(true);
        Logger.setMinimumLogLevel(LogLevel.FINEST);
        AudioManager.init();
        AudioManager.setListenerData(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        AudioManager.loadSound("bounce", "/de/omnikryptec/old/audio/bounce.wav");
        //AudioManager.loadSound("Tobu_-_Infectious_[NCS_Release]", "/de/omnikryptec/audio/Tobu_-_Infectious_[NCS_Release].wav");
        final AudioSource source = new AudioSource();
        timer.start();
        // source.setLooping(true);
        // source.play("bounce");
        final AdvancedFile file = new AdvancedFile(true, "", "de", "omnikryptec", "audio", "Tobu_-_Infectious_[NCS_Release].wav");
        final StreamedSound streamedSound = StreamedSound.ofAdvancedFile("Tobu_-_Infectious_[NCS_Release]_streamed", source, file);
        AudioManager.loadSound("Tobu_-_Infectious_[NCS_Release]_cached", file);
        final Sound sound = AudioManager.getSound("Tobu_-_Infectious_[NCS_Release]_cached");
        //source.play(streamedSound);

        AudioPlaylist playlist = new AudioPlaylist(AudioManager.getSound("bounce"), sound, streamedSound);

        Logger.log(Arrays.toString(playlist.getSounds()));
        playlist.shuffle();
        Logger.log(Arrays.toString(playlist.getSounds()));

        playlist.start();

        // source.play("Tobu_-_Infectious_[NCS_Release]");
        final int startX = 100;
        Vector3f position = new Vector3f(startX, 0, 2);
        source.setPosition(position);
        Logger.log("Test 1");
        while (position.x > -startX) {
            // Logger.log("Test 2");
            try {
                // position.x -= 0.03F;
                position.x -= 0.1F;
                source.setPosition(position);
                Thread.sleep(10);
            } catch (Exception ex) {
                Logger.logErr("Error: " + ex, ex);
            }
        }
        Logger.log("Test 3");
        source.delete();
        Logger.log("Test 4");
        AudioManager.cleanup();
        Logger.log("Cleaned Up successfully!");
        Commands.COMMANDEXIT.run("-java");
    }

}
