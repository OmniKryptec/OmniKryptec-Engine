package omnikryptec.util;

import omnikryptec.camera.Camera;
import omnikryptec.settings.KeySettings;
import org.lwjgl.input.Keyboard;

/**
 * 
 * @author pcfreak9000
 *
 */
public class InputUtil {

    private static String s = "";
    private static boolean disableautoin = false;

    /**
     * all keys that are down this frame only active if
     * <code>isAutoKeyboardEventReadDisabled</code> is false
     * 
     * @return
     */
    public static String keysDown() {
        return s;
    }

    private static final boolean[] keys = new boolean[Keyboard.KEYBOARD_SIZE];

    /**
     * called from the engine; computes keyboardevents
     */
    public static void nextFrame() {
        if (!disableautoin) {
            s = "";
            while (Keyboard.next()) {
                s += Keyboard.getEventCharacter();
                keys[Keyboard.getEventKey()] = Keyboard.getEventKeyState();
            }
        }
    }

    /**
     * dont read the keyevents from the keyboard automatically (def=false)
     * 
     * @param b
     */
    public static void disableAutoKeyboardEventRead(boolean b) {
        disableautoin = b;
    }

    /**
     * dont read the keyevents from the keyboard automatically? (def=false)
     * 
     * @return
     */
    public static boolean isAutoKeyboardEventReadDisabled() {
        return disableautoin;
    }

    /**
     * is the specified key down? only active if
     * <code>isAutoKeyboardEventReadDisabled</code> is false
     * 
     * @param key
     * @return
     */
    public static boolean isKeyDown(int key) {
        return keys[key];
    }
    
    /**
     * Processes keys to a camera
     * @param camera Camera to be moved
     * @param deltaPos The delta for the Position
     * @param deltaRot The delta for the Rotation
     * @return 
     */
    public static Camera doCameraLogic(Camera camera, KeySettings keySettings, final float deltaPos, final float deltaRot) {
        final float deltaPosForward = (InputUtil.isKeyDown(keySettings.getMoveForward()) ? deltaPos : 0) + (InputUtil.isKeyDown(keySettings.getMoveBackward()) ? -deltaPos : 0);
        final float deltaPosSideward = (InputUtil.isKeyDown(keySettings.getMoveRight()) ? deltaPos : 0) + (InputUtil.isKeyDown(keySettings.getMoveLeft()) ? -deltaPos : 0);
        final float deltaPosUpward = (InputUtil.isKeyDown(keySettings.getMoveUp()) ? deltaPos : 0) + (InputUtil.isKeyDown(keySettings.getMoveDown()) ? -deltaPos : 0);
        camera.moveSpace(deltaPosForward, deltaPosSideward, deltaPosUpward);
        final float deltaRotX = (InputUtil.isKeyDown(keySettings.getTurnPitchUp()) ? -deltaRot : 0) + (InputUtil.isKeyDown(keySettings.getTurnPitchDown()) ? deltaRot : 0);
        final float deltaRotY = (InputUtil.isKeyDown(keySettings.getTurnYawLeft()) ? -deltaRot : 0) + (InputUtil.isKeyDown(keySettings.getTurnYawRight()) ? deltaRot : 0);
        final float deltaRotZ = (InputUtil.isKeyDown(keySettings.getTurnRollLeft()) ? -deltaRot : 0) + (InputUtil.isKeyDown(keySettings.getTurnRollRight()) ? deltaRot : 0);
        camera.increaseRelativeRot(deltaRotX, deltaRotY, deltaRotZ);
        return camera;
    }
    
}
