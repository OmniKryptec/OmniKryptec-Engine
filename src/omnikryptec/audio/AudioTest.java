package omnikryptec.audio;

import omnikryptec.logger.Logger;
import omnikryptec.util.NativesLoader;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Panzer1119
 */
public class AudioTest {
    
    public static void main(String[] args) {
        NativesLoader.loadNatives();
        AudioManager.init();
        AudioManager.setListenerData(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        AudioManager.loadSound("bounce", "/omnikryptec/audio/bounce.wav");
        final AudioSource source = new AudioSource();
        source.setLooping(true);
        source.play("bounce");
        Vector3f position = new Vector3f(8, 0, 2);
        source.setPosition(position);
        while(position.x > -8) {
            try {
                position.x -= 0.03F;
                source.setPosition(position);
                Thread.sleep(10);
            } catch (Exception ex) {
                Logger.logErr("Error: " + ex, ex);
            }
        }
        source.delete();
        AudioManager.cleanup();
        Logger.log("Cleaned Up successfully!");
    }
    
}
