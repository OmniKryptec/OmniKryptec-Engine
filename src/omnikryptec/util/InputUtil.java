package omnikryptec.util;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.GameObject;
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
     * Processes keys to a GameObject
     * @param go GameObject to be moved
     * @param vps The delta for the Position
     * @param dps The delta for the Rotation
     * @return 
     */
    public static GameObject doFirstPersonController(GameObject go, KeySettings keySettings, float vps, float dps) {
    	final float dt = DisplayManager.instance().getDeltaTime();
    	vps *= dt;
    	dps *= dt;
        final float deltaPosForward = (InputUtil.isKeyboardKeyDown(keySettings.getMoveForward()) ? vps : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getMoveBackward()) ? -vps : 0);
        final float deltaPosSideward = (InputUtil.isKeyboardKeyDown(keySettings.getMoveRight()) ? vps : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getMoveLeft()) ? -vps : 0);
        final float deltaPosUpward = (InputUtil.isKeyboardKeyDown(keySettings.getMoveUp()) ? vps : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getMoveDown()) ? -vps : 0);
        moveXYZ(go, deltaPosForward, deltaPosSideward, deltaPosUpward);
        final float deltaRotX = (InputUtil.isKeyboardKeyDown(keySettings.getTurnPitchUp()) ? -dps : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getTurnPitchDown()) ? dps : 0);
        final float deltaRotY = (InputUtil.isKeyboardKeyDown(keySettings.getTurnYawLeft()) ? -dps : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getTurnYawRight()) ? dps : 0);
        final float deltaRotZ = (InputUtil.isKeyboardKeyDown(keySettings.getTurnRollLeft()) ? -dps : 0) + (InputUtil.isKeyboardKeyDown(keySettings.getTurnRollRight()) ? dps : 0);
        go.increaseRelativeRot(deltaRotX, deltaRotY, deltaRotZ);
        return go;
    }
    
    /**
     * Moves the object the given distances, with ignoring the pitch and roll of the object
     * @param forward  Positive = Forward, Negative = Backward
     * @param sideward Positive = Right,   Negative = Left
     * @param upward   Positive = Up,      Negative = Down
     */
    public static void moveXZ(GameObject go, float forward, float sideward, float upward) {
        if(forward != 0) {
            go.increaseRelativePos((float) (forward * Math.sin(Math.toRadians(go.getAbsoluteRotation().y))), 0, (float) (-forward * Math.cos(Math.toRadians(go.getAbsoluteRotation().y))));
        }
        if(sideward != 0) {
            go.increaseRelativePos((float) (sideward * Math.cos(Math.toRadians(go.getAbsoluteRotation().y))), 0, (float) (sideward * Math.sin(Math.toRadians(go.getAbsoluteRotation().y))));
        }
        if(upward != 0) {
            go.increaseRelativePos(0, upward, 0);
        }
    }
    
    /**
     * Moves the object the given distances, with using the pitch and roll of the object
     * @param forward  Positive = Forward, Negative = Backward
     * @param sideward Positive = Right,   Negative = Left
     * @param upward   Positive = Up,      Negative = Down
     */
    public static void moveXYZ(GameObject go, float forward, float sideward, float upward) {
        if(forward != 0) {
            go.increaseRelativePos((float) (forward * Math.sin(Math.toRadians(go.getAbsoluteRotation().y))), (float) (-forward * Math.sin(Math.toRadians(go.getAbsoluteRotation().x))), (float) (-forward * Math.cos(Math.toRadians(go.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(go.getAbsoluteRotation().x))));
        }
        if(sideward != 0) {
            go.increaseRelativePos((float) (sideward * Math.cos(Math.toRadians(go.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(go.getAbsoluteRotation().z))), (float) (-sideward * Math.sin(Math.toRadians(go.getAbsoluteRotation().z))), (float) (sideward * Math.sin(Math.toRadians(go.getAbsoluteRotation().y))));
        }
        if(upward != 0) {
            go.increaseRelativePos((float) (upward * Math.sin(Math.toRadians(go.getAbsoluteRotation().z))), (float) (upward * Math.cos(Math.toRadians(go.getAbsoluteRotation().x)) * Math.cos(Math.toRadians(go.getAbsoluteRotation().z))), (float) (-upward * Math.sin(Math.toRadians(go.getAbsoluteRotation().x))));
        }
    }
}
