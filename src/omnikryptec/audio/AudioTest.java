package omnikryptec.audio;

import java.util.Arrays;

import javax.swing.Timer;

import org.joml.Vector3f;

import de.codemakers.io.file.AdvancedFile;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.logger.Commands;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

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
        AudioManager.loadSound("bounce", "/omnikryptec/audio/bounce.wav");
        //AudioManager.loadSound("Tobu_-_Infectious_[NCS_Release]", "/omnikryptec/audio/Tobu_-_Infectious_[NCS_Release].wav");
        final AudioSource source = new AudioSource();
        timer.start();
        // source.setLooping(true);
        // source.play("bounce");
        final AdvancedFile file = new AdvancedFile(true, "", "omnikryptec", "audio", "Tobu_-_Infectious_[NCS_Release].wav");
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
