package omnikryptec.util;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.entity.GameObject;
import omnikryptec.settings.KeySettings;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * 
 * @author pcfreak9000
 *
 */
public class InputUtil {
    
    private static String keyboardKeys_buffer = "";

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
    private static final Vector2f mousePosition = new Vector2f(0, 0);
    private static final Vector3f mouseDelta = new Vector3f(0, 0, 0);
    private static boolean isMouseGrabbed = false;
    private static boolean isMouseInsideWindow = false;
    private static final Vector3f currentRay = new Vector3f(0, 0, 0);
    private static Camera camera = null;
    private static Matrix4f invertedProjectionMatrix = null;
    private static Matrix4f invertedViewMatrix = new Matrix4f();

    /**
     * called from the engine; computes keyboardevents
     */
    public static void nextFrame() {
        for(int i = 0; i < keys_mouse.length; i++) {
            keys_mouse[i] = Mouse.isButtonDown(i);
        }
        isMouseGrabbed = Mouse.isGrabbed();
        isMouseInsideWindow = Mouse.isInsideWindow();
        mousePosition.x = Mouse.getX();
        mousePosition.y = Mouse.getY();
        mouseDelta.x = Mouse.getDX();
        mouseDelta.y = Mouse.getDY();
        mouseDelta.z = Mouse.getDWheel();
        keyboardKeys_buffer = "";
        while(Keyboard.next()) {
            keyboardKeys_buffer += Keyboard.getEventCharacter();
            keys_keyboard[Keyboard.getEventKey()] = Keyboard.getEventKeyState();
        }
        if(camera != null && invertedProjectionMatrix != null) {
            Matrix4f.invert(camera.getViewMatrix(), invertedViewMatrix);
            calculateMouseRay();
        }
    }

    /**
     * is the specified key down? only active if
     * <code>isAutoKeyboardEventReadDisabled</code> is false
     * 
     * @param key
     * @return
     */
    public static boolean isKeyboardKeyDown(int key) {
        if(key < 0 || key >= keys_keyboard.length) {
            return false;
        }
        return keys_keyboard[key];
    }
    
    public static boolean isMouseKeyDown(int key) {
        if(key < 0 || key >= keys_mouse.length) {
            return false;
        }
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

    public static Matrix4f getInvertedProjectionMatrix() {
        return invertedProjectionMatrix;
    }

    public static void setInvertedProjectionMatrix(Matrix4f invertedProjectionMatrix) {
        InputUtil.invertedProjectionMatrix = invertedProjectionMatrix;
    }

    public static Matrix4f getInvertedViewMatrix() {
        return invertedViewMatrix;
    }

    public static void setInvertedViewMatrix(Matrix4f invertedViewMatrix) {
        InputUtil.invertedViewMatrix = invertedViewMatrix;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static void setCamera(Camera camera) {
        InputUtil.camera = camera;
    }

    public static Vector3f getCurrentRay() {
        return currentRay;
    }
    
    private static void calculateMouseRay() {
        final Vector2f normalizedDevicePosition = new Vector2f((2.0F * mousePosition.x) / Display.getWidth() - 1, (2.0F * mousePosition.y) / Display.getHeight() - 1);
        final Vector4f clipSpacePosition = new Vector4f(normalizedDevicePosition.x, normalizedDevicePosition.y, -1F, 1F);
        Matrix4f.transform(invertedProjectionMatrix, clipSpacePosition, clipSpacePosition);
        final Vector4f eyePosition = new Vector4f(clipSpacePosition.x, clipSpacePosition.y, -1F, 0);
        Matrix4f.transform(invertedViewMatrix, eyePosition, eyePosition);
        final Vector3f worldPosition = new Vector3f(eyePosition.x, eyePosition.y, eyePosition.z);
        worldPosition.normalise();
        currentRay.x = worldPosition.x;
        currentRay.y = worldPosition.y;
        currentRay.z = worldPosition.z;
    }
    
    /**
     * Processes keys to a GameObject
     * @param go GameObject to be moved
     * @param vps The delta for the Position
     * @param dps The delta for the Rotation
     * @return 
     */
    public static GameObject doFirstPersonController(GameObject gameObject, KeySettings keySettings, float vps, float dps) {
    	final float dt = DisplayManager.instance().getDeltaTime();
    	vps *= dt;
    	dps *= dt;
        final float deltaPosForward = (keySettings.getKey("moveForward").isPressed() ? vps : 0) + (keySettings.getKey("moveBackward").isPressed() ? -vps : 0);
        final float deltaPosSideward = (keySettings.getKey("moveRight").isPressed() ? vps : 0) + (keySettings.getKey("moveLeft").isPressed() ? -vps : 0);
        final float deltaPosUpward = (keySettings.getKey("moveUp").isPressed() ? vps : 0) + (keySettings.getKey("moveDown").isPressed() ? -vps : 0);
        moveXYZ(gameObject, gameObject, deltaPosForward, deltaPosSideward, deltaPosUpward);
        final float deltaRotX = (keySettings.getKey("turnPitchUp").isPressed() ? -dps : 0) + (keySettings.getKey("turnPitchDown").isPressed() ? dps : 0);
        final float deltaRotY = (keySettings.getKey("turnYawLeft").isPressed() ? -dps : 0) + (keySettings.getKey("turnYawRight").isPressed() ? dps : 0);
        final float deltaRotZ = (keySettings.getKey("turnRollLeft").isPressed() ? -dps : 0) + (keySettings.getKey("turnRollRight").isPressed() ? dps : 0);
        turnXYZ(gameObject, gameObject, deltaRotX, deltaRotY, deltaRotZ);
        return gameObject;
    }
    
    /**
     * Processes keys to a GameObject
     * @param source GameObject relative to the GameObject which gets moved
     * @param destination GameObject to be moved
     * @param vps The delta for the Position
     * @param dps The delta for the Rotation
     * @return 
     */
    public static GameObject doThirdPersonController(GameObject source, GameObject destination, KeySettings keySettings, float vps, float dps) {
    	final float dt = DisplayManager.instance().getDeltaTime();
    	vps *= dt;
    	dps *= dt;
        final float deltaPosForward = (keySettings.getKey("moveForward").isPressed() ? vps : 0) + (keySettings.getKey("moveBackward").isPressed() ? -vps : 0);
        final float deltaPosSideward = (keySettings.getKey("moveRight").isPressed() ? vps : 0) + (keySettings.getKey("moveLeft").isPressed() ? -vps : 0);
        final float deltaPosUpward = (keySettings.getKey("moveUp").isPressed() ? vps : 0) + (keySettings.getKey("moveDown").isPressed() ? -vps : 0);
        moveXZ(source, destination, deltaPosForward, deltaPosSideward, deltaPosUpward);
        final float deltaRotX = (keySettings.getKey("turnPitchUp").isPressed() ? -dps : 0) + (keySettings.getKey("turnPitchDown").isPressed() ? dps : 0);
        final float deltaRotY = (keySettings.getKey("turnYawLeft").isPressed() ? -dps : 0) + (keySettings.getKey("turnYawRight").isPressed() ? dps : 0);
        final float deltaRotZ = (keySettings.getKey("turnRollLeft").isPressed() ? -dps : 0) + (keySettings.getKey("turnRollRight").isPressed() ? dps : 0);
        turnNormal(source, destination, deltaRotX, deltaRotY, deltaRotZ);
        return destination;
    }
    
    /**
     * Moves the object the given distances, with ignoring the pitch and roll of the object
     * @param forward  Positive = Forward, Negative = Backward
     * @param sideward Positive = Right,   Negative = Left
     * @param upward   Positive = Up,      Negative = Down
     */
    public static void moveXZ(GameObject source, GameObject destination, float forward, float sideward, float upward) {
        if(forward != 0) {
            destination.increaseRelativePos((float) (forward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))), 0, (float) (-forward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if(sideward != 0) {
            destination.increaseRelativePos((float) (sideward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))), 0, (float) (sideward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if(upward != 0) {
            destination.increaseRelativePos(0, upward, 0);
        }
    }
    
    /**
     * Moves the object the given distances, with using the pitch and roll of the object
     * @param forward  Positive = Forward, Negative = Backward
     * @param sideward Positive = Right,   Negative = Left
     * @param upward   Positive = Up,      Negative = Down
     */
    public static void moveXYZ(GameObject source, GameObject destination, float forward, float sideward, float upward) {
        if(forward != 0) {
            destination.increaseRelativePos((float) (forward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))), (float) (-forward * Math.sin(Math.toRadians(source.getAbsoluteRotation().x))), (float) (-forward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().x))));
        }
        if(sideward != 0) {
            destination.increaseRelativePos((float) (sideward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().z))), (float) (-sideward * Math.sin(Math.toRadians(source.getAbsoluteRotation().z))), (float) (sideward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if(upward != 0) {
            destination.increaseRelativePos((float) (upward * Math.sin(Math.toRadians(source.getAbsoluteRotation().z))), (float) (upward * Math.cos(Math.toRadians(source.getAbsoluteRotation().x)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().z))), (float) (-upward * Math.sin(Math.toRadians(source.getAbsoluteRotation().x))));
        }
    }
    
    public static void turnXZ(GameObject source, GameObject destination, float deltaRotX, float deltaRotY, float deltaRotZ) {
        if(deltaRotZ != 0) {
            destination.increaseRelativeRot((float) (deltaRotZ * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))), 0, (float) (-deltaRotZ * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if(deltaRotX != 0) {
            destination.increaseRelativeRot((float) (deltaRotX * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))), 0, (float) (deltaRotX * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if(deltaRotY != 0) {
            destination.increaseRelativeRot(0, deltaRotY, 0);
        }
    }
    
    public static void turnXZY(GameObject source, GameObject destination, float deltaRotX, float deltaRotY, float deltaRotZ) {
        if(deltaRotZ != 0) {
            destination.increaseRelativeRot((float) (-deltaRotZ * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))), (float) (deltaRotZ * Math.sin(Math.toRadians(source.getAbsoluteRotation().x))), (float) (deltaRotZ * Math.cos(Math.toRadians(source.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().x))));
        }
        if(deltaRotX != 0) {
            destination.increaseRelativeRot((float) (deltaRotX * Math.cos(Math.toRadians(source.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().z))), (float) (-deltaRotX * Math.sin(Math.toRadians(source.getAbsoluteRotation().z))), (float) (deltaRotX * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if(deltaRotY != 0) {
            destination.increaseRelativeRot(0, deltaRotY, 0);
        }
    }
    
    public static void turnXYZ(GameObject source, GameObject destination, float deltaRotX, float deltaRotY, float deltaRotZ) {
        if(deltaRotZ != 0) {
            destination.increaseRelativeRot((float) (-deltaRotZ * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))), (float) (deltaRotZ * Math.sin(Math.toRadians(source.getAbsoluteRotation().x))), (float) (deltaRotZ * Math.cos(Math.toRadians(source.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().x))));
        }
        if(deltaRotX != 0) {
            destination.increaseRelativeRot((float) (deltaRotX * Math.cos(Math.toRadians(source.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().z))), (float) (-deltaRotX * Math.sin(Math.toRadians(source.getAbsoluteRotation().z))), (float) (deltaRotX * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if(deltaRotY != 0) {
            destination.increaseRelativeRot((float) (deltaRotY * Math.sin(Math.toRadians(source.getAbsoluteRotation().z))), (float) (deltaRotY * Math.cos(Math.toRadians(source.getAbsoluteRotation().x)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().z))), (float) (-deltaRotY * Math.sin(Math.toRadians(source.getAbsoluteRotation().x))));
        }
    }
    
    public static void turnNormal(GameObject source, GameObject destination, float deltaRotX, float deltaRotY, float deltaRotZ) {
        destination.increaseRelativeRot(deltaRotX, deltaRotY, deltaRotZ);
    }
    
}
