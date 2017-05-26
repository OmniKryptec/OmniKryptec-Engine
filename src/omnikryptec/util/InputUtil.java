package omnikryptec.util;

import omnikryptec.entity.Camera;
import omnikryptec.settings.KeySettings;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * 
 * @author pcfreak9000
 *
 */
public class InputUtil {

    public static final int MOUSE_BUTTON_LEFT = 0;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    public static final int MOUSE_BUTTON_MIDDLE = 2;
    
    private static String keyboardKeys_buffer = "";
    private static boolean disableAutoInput = false;

    /**
     * all keys that are down this frame only active if
     * <code>isAutoKeyboardEventReadDisabled</code> is false
     * 
     * @return
     */
    public static String keyboardKeysDown() {
        return keyboardKeys_buffer;
    }

    private static final boolean[] keys_keyboard = new boolean[Keyboard.KEYBOARD_SIZE];
    private static final boolean[] keys_mouse = new boolean[Mouse.getButtonCount()];
    private static Vector2f mousePosition = new Vector2f(0, 0);
    private static Vector3f mouseDelta = new Vector3f(0, 0, 0);
    private static boolean isMouseGrabbed = false;
    private static boolean isMouseInsideWindow = false;

    /**
     * called from the engine; computes keyboardevents
     */
    public static void nextFrame() {
        if(!disableAutoInput) {
            for(int i = 0; i < keys_mouse.length; i++) {
                keys_mouse[i] = Mouse.isButtonDown(i);
            }
            isMouseGrabbed = Mouse.isGrabbed();
            isMouseInsideWindow = Mouse.isInsideWindow();
            mousePosition = new Vector2f(Mouse.getX(), Mouse.getY());
            mouseDelta = new Vector3f(Mouse.getDX(), Mouse.getDY(), Mouse.getDWheel());
            keyboardKeys_buffer = "";
            while(Keyboard.next()) {
                keyboardKeys_buffer += Keyboard.getEventCharacter();
                keys_keyboard[Keyboard.getEventKey()] = Keyboard.getEventKeyState();
            }
        }
    }

    /**
     * dont read the keyevents from the keyboard automatically (def=false)
     * 
     * @param b
     */
    public static void disableAutoKeyboardEventRead(boolean b) {
        disableAutoInput = b;
    }

    /**
     * dont read the keyevents from the keyboard automatically? (def=false)
     * 
     * @return
     */
    public static boolean isAutoKeyboardEventReadDisabled() {
        return disableAutoInput;
    }

    /**
     * is the specified key down? only active if
     * <code>isAutoKeyboardEventReadDisabled</code> is false
     * 
     * @param key
     * @return
     */
    public static boolean isKeyboardKeyDown(int key) {
        return keys_keyboard[key];
    }
    
    public static boolean isMouseKeyDown(int key) {
        return keys_mouse[key];
    }

    public static boolean isMouseGrabbed() {
        return isMouseGrabbed;
    }

    public static boolean isMouseInsideWindow() {
        return isMouseInsideWindow;
    }

    public static Vector2f getMousePosition() {
        return mousePosition;
    }

    public static Vector3f getMouseDelta() {
        return mouseDelta;
    }
    
    /**
     * Processes keys to a camera
     * @param camera Camera to be moved
     * @param deltaPos The delta for the Position
     * @param deltaRot The delta for the Rotation
     * @return 
     */
    public static Camera doCameraLogic(Camera camera, KeySettings keySettings, final float deltaPos, final float deltaRot) {
        final float deltaPosForward = (InputUtil.isKeyboardKeyDown(keySettings.getMoveForward()) ? deltaPos : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getMoveBackward()) ? -deltaPos : 0);
        final float deltaPosSideward = (InputUtil.isKeyboardKeyDown(keySettings.getMoveRight()) ? deltaPos : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getMoveLeft()) ? -deltaPos : 0);
        final float deltaPosUpward = (InputUtil.isKeyboardKeyDown(keySettings.getMoveUp()) ? deltaPos : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getMoveDown()) ? -deltaPos : 0);
        camera.moveSpace(deltaPosForward, deltaPosSideward, deltaPosUpward);
        final float deltaRotX = (InputUtil.isKeyboardKeyDown(keySettings.getTurnPitchUp()) ? -deltaRot : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getTurnPitchDown()) ? deltaRot : 0);
        final float deltaRotY = (InputUtil.isKeyboardKeyDown(keySettings.getTurnYawLeft()) ? -deltaRot : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getTurnYawRight()) ? deltaRot : 0);
        final float deltaRotZ = (InputUtil.isKeyboardKeyDown(keySettings.getTurnRollLeft()) ? -deltaRot : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getTurnRollRight()) ? deltaRot : 0);
        camera.increaseRelativeRot(deltaRotX, deltaRotY, deltaRotZ);
        return camera;
    }
    
}
