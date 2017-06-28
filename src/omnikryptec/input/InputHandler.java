package omnikryptec.input;

import omnikryptec.display.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.entity.GameObject;
import omnikryptec.settings.KeySettings;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Panzer1119 &amp; pcfreak9000
 *
 */
public class InputHandler {

    private static long window = -1;
    private static boolean longButtonPressEnabled = false;
    private static float currentTime = 0;

    static {
        window = Display.getID();
    }
    
    public static void nextFrame() {
        GLFW.glfwPollEvents();
        JoystickHandler.updateAll();
        /*
        final KeySettings keySettings = DisplayManager.instance().getSettings().getKeySettings();
        for (int i = 0; i < keys_mouse.length; i++) {
            final boolean temp = Mouse.isButtonDown(i);
            if (longButtonPressEnabled) {
                if (keys_mouse[i] != temp) {
                    keySettings.updateKeys(currentTime, i, false);
                }
            }
            keys_mouse[i] = temp;
        }
        isMouseGrabbed = Mouse.isGrabbed();
        isMouseInsideWindow = Mouse.isInsideWindow();
        mousePosition.x = Mouse.getX();
        mousePosition.y = Mouse.getY();
        mouseDelta.x = Mouse.getDX();
        mouseDelta.y = Mouse.getDY();
        mouseDelta.z = Mouse.getDWheel();
        keyboardKeys_buffer = "";
        while (Keyboard.next()) {
            keyboardKeys_buffer += Keyboard.getEventCharacter();
            final int eventKey = Keyboard.getEventKey();
            final boolean eventKeyState = Keyboard.getEventKeyState();
            if (longButtonPressEnabled) {
                if (keys_keyboard[eventKey] != eventKeyState) {
                    keySettings.updateKeys(currentTime, eventKey, true);
                }
            }
            keys_keyboard[eventKey] = eventKeyState;
        }
        if (camera != null) {
            if (invertedProjectionMatrix == null) {
                invertedProjectionMatrix = Matrix4f.invert(camera.getProjectionMatrix(), null);
            }
            Matrix4f.invert(camera.getViewMatrix(), invertedViewMatrix);
            calculateMouseRay();
        }
        currentTime = DisplayManager.instance().getCurrentTime();
        */
    }
    
    public static boolean isKeyboardKeyDown(int key) {
        if (key < 0 || key >= keys_keyboard.length) {
            return false;
        }
        return keys_keyboard[key];
    }

    public static boolean isMouseKeyDown(int key) {
        if (key < 0 || key >= keys_mouse.length) {
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

    public static boolean isLongButtonPressEnabled() {
        return longButtonPressEnabled;
    }

    public static void setLongButtonPressEnabled(boolean longButtonPressEnabled) {
        InputHandler.longButtonPressEnabled = longButtonPressEnabled;
    }

    public static Matrix4f getInvertedProjectionMatrix() {
        return invertedProjectionMatrix;
    }

    public static void setInvertedProjectionMatrix(Matrix4f invertedProjectionMatrix) {
        InputHandler.invertedProjectionMatrix = invertedProjectionMatrix;
    }

    public static Matrix4f getInvertedViewMatrix() {
        return invertedViewMatrix;
    }

    public static void setInvertedViewMatrix(Matrix4f invertedViewMatrix) {
        InputHandler.invertedViewMatrix = invertedViewMatrix;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static void setCamera(Camera camera) {
        InputHandler.camera = camera;
    }

    public static Vector3f getCurrentRay() {
        return currentRay;
    }

    private static void calculateMouseRay() {
        final Vector2f normalizedDevicePosition = new Vector2f((2.0F * mousePosition.x) / Display.getWidth() - 1,
                (2.0F * mousePosition.y) / Display.getHeight() - 1);
        final Vector4f clipSpacePosition = new Vector4f(normalizedDevicePosition.x, normalizedDevicePosition.y, -1F,
                1F);
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
     *
     * @param gameObject GameObject to be moved
     *
     * @return
     */
    public static GameObject doFirstPersonController(GameObject gameObject, KeySettings keySettings,
            float deltaPosXZSpeed, float deltaPosYSpeed, float deltaRotXYZSpeed, boolean space) {
        final float dt = DisplayManager.instance().getDeltaTimef();
        deltaPosXZSpeed *= dt;
        deltaPosYSpeed *= dt;
        deltaRotXYZSpeed *= dt;
        final float deltaPosForward = (keySettings.getKey("moveForward").isPressed() ? deltaPosXZSpeed : 0)
                + (keySettings.getKey("moveBackward").isPressed() ? -deltaPosXZSpeed : 0);
        final float deltaPosSideward = (keySettings.getKey("moveRight").isPressed() ? deltaPosXZSpeed : 0)
                + (keySettings.getKey("moveLeft").isPressed() ? -deltaPosXZSpeed : 0);
        final float deltaPosUpward = (keySettings.getKey("moveUp").isPressed() ? deltaPosYSpeed : 0)
                + (keySettings.getKey("moveDown").isPressed() ? -deltaPosYSpeed : 0);
        if (space) {
            moveXYZ(gameObject, gameObject, deltaPosForward, deltaPosSideward, deltaPosUpward);
        } else {
            moveXZ(gameObject, gameObject, deltaPosForward, deltaPosSideward, deltaPosUpward);
        }
        final float deltaRotX = (keySettings.getKey("turnPitchUp").isPressed() ? -deltaRotXYZSpeed : 0)
                + (keySettings.getKey("turnPitchDown").isPressed() ? deltaRotXYZSpeed : 0);
        final float deltaRotY = (keySettings.getKey("turnYawLeft").isPressed() ? -deltaRotXYZSpeed : 0)
                + (keySettings.getKey("turnYawRight").isPressed() ? deltaRotXYZSpeed : 0);
        final float deltaRotZ = (keySettings.getKey("turnRollLeft").isPressed() ? -deltaRotXYZSpeed : 0)
                + (keySettings.getKey("turnRollRight").isPressed() ? deltaRotXYZSpeed : 0);
        if (space) {
            turnXYZ(gameObject, gameObject, deltaRotX, deltaRotY, deltaRotZ);
        } else {
            turnXZ(gameObject, gameObject, deltaRotX, deltaRotY, deltaRotZ);
        }
        return gameObject;
    }

    /**
     * Processes keys to a GameObject
     *
     * @param source GameObject relative to the GameObject which gets moved
     * @param destination GameObject to be moved
     *
     * @return
     */
    public static GameObject doThirdPersonController(GameObject source, GameObject destination, KeySettings keySettings,
            float deltaPosXZSpeed, float deltaPosYSpeed, float deltaRotXYZSpeed) {
        final float dt = DisplayManager.instance().getDeltaTimef();
        deltaPosXZSpeed *= dt;
        deltaPosYSpeed *= dt;
        deltaRotXYZSpeed *= dt;
        final float deltaPosForward = (keySettings.getKey("moveForward").isPressed() ? deltaPosXZSpeed : 0)
                + (keySettings.getKey("moveBackward").isPressed() ? -deltaPosXZSpeed : 0);
        final float deltaPosSideward = (keySettings.getKey("moveRight").isPressed() ? deltaPosXZSpeed : 0)
                + (keySettings.getKey("moveLeft").isPressed() ? -deltaPosXZSpeed : 0);
        final float deltaPosUpward = (keySettings.getKey("moveUp").isPressed() ? deltaPosYSpeed : 0)
                + (keySettings.getKey("moveDown").isPressed() ? -deltaPosYSpeed : 0);
        moveXZ(source, destination, deltaPosForward, deltaPosSideward, deltaPosUpward);
        final float deltaRotX = (keySettings.getKey("turnPitchUp").isPressed() ? -deltaRotXYZSpeed : 0)
                + (keySettings.getKey("turnPitchDown").isPressed() ? deltaRotXYZSpeed : 0);
        final float deltaRotY = (keySettings.getKey("turnYawLeft").isPressed() ? -deltaRotXYZSpeed : 0)
                + (keySettings.getKey("turnYawRight").isPressed() ? deltaRotXYZSpeed : 0);
        final float deltaRotZ = (keySettings.getKey("turnRollLeft").isPressed() ? -deltaRotXYZSpeed : 0)
                + (keySettings.getKey("turnRollRight").isPressed() ? deltaRotXYZSpeed : 0);
        turnNormal(source, destination, deltaRotX, deltaRotY, deltaRotZ);
        return destination;
    }

    /**
     * Moves the object the given distances, with ignoring the pitch and roll of
     * the object
     *
     * @param forward Positive = Forward, Negative = Backward
     * @param sideward Positive = Right, Negative = Left
     * @param upward Positive = Up, Negative = Down
     */
    public static void moveXZ(GameObject source, GameObject destination, float forward, float sideward, float upward) {
        if (forward != 0) {
            destination.increaseRelativePos(
                    (float) (forward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))), 0,
                    (float) (-forward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if (sideward != 0) {
            destination.increaseRelativePos(
                    (float) (sideward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))), 0,
                    (float) (sideward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if (upward != 0) {
            destination.increaseRelativePos(0, upward, 0);
        }
    }

    /**
     * Moves the object the given distances, with using the pitch and roll of
     * the object
     *
     * @param forward Positive = Forward, Negative = Backward
     * @param sideward Positive = Right, Negative = Left
     * @param upward Positive = Up, Negative = Down
     */
    public static void moveXYZ(GameObject source, GameObject destination, float forward, float sideward, float upward) {
        if (forward != 0) {
            destination.increaseRelativePos(
                    (float) (forward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))),
                    (float) (-forward * Math.sin(Math.toRadians(source.getAbsoluteRotation().x))),
                    (float) (-forward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))
                    * Math.cos(Math.toRadians(source.getAbsoluteRotation().x))));
        }
        if (sideward != 0) {
            destination.increaseRelativePos(
                    (float) (sideward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))
                    * Math.cos(Math.toRadians(source.getAbsoluteRotation().z))),
                    (float) (-sideward * Math.sin(Math.toRadians(source.getAbsoluteRotation().z))),
                    (float) (sideward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if (upward != 0) {
            destination.increaseRelativePos((float) (upward * Math.sin(Math.toRadians(source.getAbsoluteRotation().z))),
                    (float) (upward * Math.cos(Math.toRadians(source.getAbsoluteRotation().x))
                    * Math.cos(Math.toRadians(source.getAbsoluteRotation().z))),
                    (float) (-upward * Math.sin(Math.toRadians(source.getAbsoluteRotation().x))));
        }
    }

    public static void turnXZ(GameObject source, GameObject destination, float deltaRotX, float deltaRotY,
            float deltaRotZ) {
        final Vector3f rotation = new Vector3f(source.getAbsoluteRotation());
        if (deltaRotZ != 0) {
            destination.increaseRelativeRot((float) (-deltaRotZ * Math.sin(Math.toRadians(rotation.y))), 0,
                    (float) (deltaRotZ * Math.cos(Math.toRadians(rotation.y))));
        }
        if (deltaRotX != 0) {
            destination.increaseRelativeRot((float) (-deltaRotX * Math.cos(Math.toRadians(rotation.y))), 0,
                    (float) (deltaRotX * Math.sin(Math.toRadians(rotation.y))));
        }
        if (deltaRotY != 0) {
            destination.increaseRelativeRot(0, deltaRotY, 0);
        }
    }

    public static void turnXZY(GameObject source, GameObject destination, float deltaRotX, float deltaRotY,
            float deltaRotZ) {
        final Vector3f rotation = new Vector3f(source.getAbsoluteRotation());
        if (deltaRotZ != 0) {
            destination.increaseRelativeRot((float) (-deltaRotZ * Math.sin(Math.toRadians(rotation.y))),
                    (float) (deltaRotZ * Math.sin(Math.toRadians(rotation.x))),
                    (float) (deltaRotZ * Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))));
        }
        if (deltaRotX != 0) {
            destination.increaseRelativeRot(
                    (float) (deltaRotX * Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.z))),
                    (float) (-deltaRotX * Math.sin(Math.toRadians(rotation.z))),
                    (float) (deltaRotX * Math.sin(Math.toRadians(rotation.y))));
        }
        if (deltaRotY != 0) {
            destination.increaseRelativeRot(0, deltaRotY, 0);
        }
    }

    public static void turnXYZ(GameObject source, GameObject destination, float deltaRotX, float deltaRotY,
            float deltaRotZ) {
        final Vector3f rotation = new Vector3f(source.getAbsoluteRotation());
        if (deltaRotZ != 0) {
            destination.increaseRelativeRot((float) (-deltaRotZ * Math.sin(Math.toRadians(rotation.y))),
                    (float) (deltaRotZ * Math.sin(Math.toRadians(rotation.x))),
                    (float) (deltaRotZ * Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))));
        }
        if (deltaRotX != 0) {
            destination.increaseRelativeRot(
                    (float) (deltaRotX * Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.z))),
                    (float) (-deltaRotX * Math.sin(Math.toRadians(rotation.z))),
                    (float) (deltaRotX * Math.sin(Math.toRadians(rotation.y))));
        }
        if (deltaRotY != 0) {
            destination.increaseRelativeRot((float) (deltaRotY * Math.sin(Math.toRadians(rotation.z))),
                    (float) (deltaRotY * Math.cos(Math.toRadians(rotation.x)) * Math.cos(Math.toRadians(rotation.z))),
                    (float) (-deltaRotY * Math.sin(Math.toRadians(rotation.x))));
        }
    }

    public static void turnNormal(GameObject source, GameObject destination, float deltaRotX, float deltaRotY,
            float deltaRotZ) {
        destination.increaseRelativeRot(deltaRotX, deltaRotY, deltaRotZ);
    }

}
