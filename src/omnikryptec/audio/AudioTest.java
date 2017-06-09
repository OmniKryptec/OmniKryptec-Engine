package omnikryptec.audio;

import javax.swing.Timer;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.logger.Commands;
import omnikryptec.logger.Logger;
import omnikryptec.util.NativesLoader;

/**
 * Test class for the audio functions
 * @author Panzer1119
 */
public class AudioTest {
    
    private static final Timer timer = new Timer(10, (e) -> {
        AudioManager.update(0);
    });
    
    public static void main(String[] args) {
        NativesLoader.loadNatives();
        AudioManager.init();
        AudioManager.setListenerData(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        AudioManager.loadSound("bounce", "/omnikryptec/audio/bounce.wav");
        //AudioManager.loadSound("Tobu_-_Infectious_[NCS_Release]", "/omnikryptec/audio/Tobu_-_Infectious_[NCS_Release].wav");
        final AudioSource source = new AudioSource();
        timer.start();
        //source.setLooping(true);
        //source.play("bounce");
        StreamedSound streamedSound = StreamedSound.ofInputStream("Tobu_-_Infectious_[NCS_Release]", source, AudioTest.class.getResourceAsStream("/omnikryptec/audio/Tobu_-_Infectious_[NCS_Release].wav"));
        source.play(streamedSound);
        //source.play("Tobu_-_Infectious_[NCS_Release]");
        final int startX = 100;
        Vector3f position = new Vector3f(startX, 0, 2);
        source.setPosition(position);
        Logger.log("Test 1");
        while(position.x > -startX) {
            //Logger.log("Test 2");
            try {
                //position.x -= 0.03F;
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
