package omnikryptec.event.input;

import omnikryptec.display.Display;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.gameobject.Camera;
import omnikryptec.gameobject.gameobject.GameObject;
import omnikryptec.settings.KeySettings;
import omnikryptec.util.Maths;
import omnikryptec.util.logger.Logger;

import org.joml.AxisAngle4f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

/**
 * InputManager
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class InputManager {

    private static long window = -1;
    private static boolean longButtonPressEnabled = false;
    private static double currentTime = 0;

    private static final KeyboardHandler keyboardHandler;
    private static final MouseHandler mouseHandler;
    private static final Vector2f mousePosition = new Vector2f(0, 0);
    private static final Vector2f mouseScrollOffset = new Vector2f(0, 0);
    private static final Vector2f mousePosition_lastTime = new Vector2f(0, 0);
    private static final Vector2f mouseScrollOffset_lastTime = new Vector2f(0, 0);
    /**
     * x = Mouse Pos X Delta y = Mouse Pos Y Delta z = Mouse Scroll X Delta w =
     * Mouse Scroll Y Delta
     */
    private static final Vector4f mouseDelta = new Vector4f(0, 0, 0, 0);
    private static CursorType cursorType = CursorType.NORMAL;

    private static Camera camera = null;
    private static Matrix4f invertedProjectionMatrix = null;
    private static Matrix4f invertedViewMatrix = new Matrix4f();
    private static final Vector3f currentRay = new Vector3f(0, 0, 0);

    static {
        window = Display.getID();
        keyboardHandler = new KeyboardHandler(window);
        mouseHandler = new MouseHandler(window);
    }

    public static final void initCallbacks() {
        keyboardHandler.initKeybCallback();
        mouseHandler.initCallbacks();
    }

    public static final void closeCallbacks() {
        keyboardHandler.close();
        mouseHandler.close();
    }

    public static final void prePollEvents() {
        keyboardHandler.preUpdate();
        mouseHandler.preUpdate();
    }

    public static final void nextFrame() {
        currentTime = DisplayManager.instance().getCurrentTime();
        mousePosition.x = mouseHandler.position.x;
        mousePosition.y = mouseHandler.position.y;
        mouseScrollOffset.x = mouseHandler.scrollOffset.x;
        mouseScrollOffset.y = mouseHandler.scrollOffset.y;
        mouseDelta.x = (mousePosition.x - mousePosition_lastTime.x);
        mouseDelta.y = (mousePosition_lastTime.y - mousePosition.y);
        mouseDelta.z = (mouseScrollOffset.x - mouseScrollOffset_lastTime.x);
        mouseDelta.w = (mouseScrollOffset.y - mouseScrollOffset_lastTime.y);
        final KeySettings keySettings = DisplayManager.instance().getSettings().getKeySettings();
        if (longButtonPressEnabled) {
            keyboardHandler.updateKeySettings(currentTime, keySettings);
            mouseHandler.updateKeySettings(currentTime, keySettings);
            JoystickHandler.updateAll(currentTime, keySettings);
        } else {
            JoystickHandler.updateAll();
        }
        if (camera != null) {
            if (invertedProjectionMatrix == null) {
                invertedProjectionMatrix = camera.getProjectionMatrix().invert(new Matrix4f());
            }
            invertedViewMatrix = camera.getViewMatrix().invert(new Matrix4f());
            calculateMouseRay();
        }
        mousePosition_lastTime.x = mousePosition.x;
        mousePosition_lastTime.y = mousePosition.y;
        mouseScrollOffset_lastTime.x = mouseScrollOffset.x;
        mouseScrollOffset_lastTime.y = mouseScrollOffset.y;
    }

    public static final CursorType setCursorType(CursorType cursorType) {
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, cursorType.getState());
        InputManager.cursorType = cursorType;
        return InputManager.cursorType;
    }

    public static final CursorType getCursorType() {
        return cursorType;
    }

    public static final InputState getKeyboardKeyInputState(int keyCode) {
        if (keyCode < 0 || keyCode >= keyboardHandler.keys.length) {
            return null;
        }
        return keyboardHandler.getKeyState(keyCode);
    }

    public static final boolean isKeyboardKeyPressed(int keyCode) {
        if (keyCode < 0 || keyCode >= keyboardHandler.keys.length) {
            return false;
        }
        return keyboardHandler.isKeyPressed(keyCode) || keyboardHandler.isKeyRepeated(keyCode);
    }

    public static final boolean isMouseButtonPressed(int buttonCode) {
        if (buttonCode < 0 || buttonCode >= mouseHandler.buttons.length) {
            return false;
        }
        return mouseHandler.isButtonPressed(buttonCode);
    }

    public static final boolean isMouseInsideWindow() {
        return mouseHandler.insideWindow;
    }

    public static final boolean isLongButtonPressEnabled() {
        return longButtonPressEnabled;
    }

    public static final void setLongButtonPressEnabled(boolean longButtonPressEnabled) {
        InputManager.longButtonPressEnabled = longButtonPressEnabled;
    }

    public static final Matrix4f getInvertedProjectionMatrix() {
        return new Matrix4f(invertedProjectionMatrix);
    }

    public static final void setInvertedProjectionMatrix(Matrix4f invertedProjectionMatrix) {
        InputManager.invertedProjectionMatrix = invertedProjectionMatrix;
    }

    public static final Matrix4f getInvertedViewMatrix() {
        return new Matrix4f(invertedViewMatrix);
    }

    public static final void setInvertedViewMatrix(Matrix4f invertedViewMatrix) {
        InputManager.invertedViewMatrix = invertedViewMatrix;
    }

    public static final Camera getCamera() {
        return camera;
    }

    public static final void setCamera(Camera camera) {
        InputManager.camera = camera;
    }

    public static final Vector3f getCurrentRay() {
        return currentRay;
    }

    public static final long getWindow() {
        return window;
    }

    public static final double getCurrentTime() {
        return currentTime;
    }

    public static final KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }

    public static final MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    public static final Vector2f getMousePosition_lastTime() {
        return new Vector2f(mousePosition_lastTime);
    }

    public static final Vector2f getMouseScrollOffset_lastTime() {
        return new Vector2f(mouseScrollOffset_lastTime);
    }

    public static final Vector4f getMouseDelta() {
        return new Vector4f(mouseDelta);
    }

    private static final void calculateMouseRay() {
        final Vector2f normalizedDevicePosition = new Vector2f((2.0F * mouseHandler.position.x) / Display.getWidth() - 1, (2.0F * mouseHandler.position.y) / Display.getHeight() - 1);
        final Vector4f clipSpacePosition = new Vector4f(normalizedDevicePosition.x, normalizedDevicePosition.y, -1F, 1F);
        invertedProjectionMatrix.transform(clipSpacePosition, clipSpacePosition);
        final Vector4f eyePosition = new Vector4f(clipSpacePosition.x, clipSpacePosition.y, -1F, 0);
        invertedViewMatrix.transform(eyePosition, eyePosition);
        final Vector3f worldPosition = new Vector3f(eyePosition.x, eyePosition.y, eyePosition.z);
        worldPosition.normalize();
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
    public static final GameObject doFirstPersonController(GameObject gameObject, KeySettings keySettings, float deltaPosXZSpeed, float deltaPosYSpeed, float deltaRotXYZSpeed, boolean space) {
        final float dt = DisplayManager.instance().getDeltaTimef();
        deltaPosXZSpeed *= dt;
        deltaPosYSpeed *= dt;
        deltaRotXYZSpeed *= dt;
        final float deltaPosForward = (keySettings.getKey("moveForward").isPressed() ? deltaPosXZSpeed : 0) + (keySettings.getKey("moveBackward").isPressed() ? -deltaPosXZSpeed : 0);
        final float deltaPosSideward = (keySettings.getKey("moveRight").isPressed() ? deltaPosXZSpeed : 0) + (keySettings.getKey("moveLeft").isPressed() ? -deltaPosXZSpeed : 0);
        final float deltaPosUpward = (keySettings.getKey("moveUp").isPressed() ? deltaPosYSpeed : 0) + (keySettings.getKey("moveDown").isPressed() ? -deltaPosYSpeed : 0);
        if (space) {
            moveXYZ(gameObject, gameObject, deltaPosForward, deltaPosSideward, deltaPosUpward);
        } else {
            moveXZ(gameObject, gameObject, deltaPosForward, deltaPosSideward, deltaPosUpward);
        }
        final float deltaRotX = (keySettings.getKey("turnPitchUp").isPressed() ? -deltaRotXYZSpeed : 0) + (keySettings.getKey("turnPitchDown").isPressed() ? deltaRotXYZSpeed : 0);
        final float deltaRotY = (keySettings.getKey("turnYawLeft").isPressed() ? -deltaRotXYZSpeed : 0) + (keySettings.getKey("turnYawRight").isPressed() ? deltaRotXYZSpeed : 0);
        final float deltaRotZ = (keySettings.getKey("turnRollLeft").isPressed() ? -deltaRotXYZSpeed : 0) + (keySettings.getKey("turnRollRight").isPressed() ? deltaRotXYZSpeed : 0);
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
    public static final GameObject doThirdPersonController(GameObject source, GameObject destination, KeySettings keySettings, float deltaPosXZSpeed, float deltaPosYSpeed, float deltaRotXYZSpeed) {
        final float dt = DisplayManager.instance().getDeltaTimef();
        deltaPosXZSpeed *= dt;
        deltaPosYSpeed *= dt;
        deltaRotXYZSpeed *= dt;
        final float deltaPosForward = (keySettings.getKey("moveForward").isPressed() ? deltaPosXZSpeed : 0) + (keySettings.getKey("moveBackward").isPressed() ? -deltaPosXZSpeed : 0);
        final float deltaPosSideward = (keySettings.getKey("moveRight").isPressed() ? deltaPosXZSpeed : 0) + (keySettings.getKey("moveLeft").isPressed() ? -deltaPosXZSpeed : 0);
        final float deltaPosUpward = (keySettings.getKey("moveUp").isPressed() ? deltaPosYSpeed : 0) + (keySettings.getKey("moveDown").isPressed() ? -deltaPosYSpeed : 0);
        moveXZ(source, destination, deltaPosForward, deltaPosSideward, deltaPosUpward);
        final float deltaRotX = (keySettings.getKey("turnPitchUp").isPressed() ? -deltaRotXYZSpeed : 0) + (keySettings.getKey("turnPitchDown").isPressed() ? deltaRotXYZSpeed : 0);
        final float deltaRotY = (keySettings.getKey("turnYawLeft").isPressed() ? -deltaRotXYZSpeed : 0) + (keySettings.getKey("turnYawRight").isPressed() ? deltaRotXYZSpeed : 0);
        final float deltaRotZ = (keySettings.getKey("turnRollLeft").isPressed() ? -deltaRotXYZSpeed : 0) + (keySettings.getKey("turnRollRight").isPressed() ? deltaRotXYZSpeed : 0);
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
    public static final void moveXZ(GameObject source, GameObject destination, float forward, float sideward, float upward) {
        if (forward != 0) {
            destination.increaseRelativePos((float) (forward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))), 0, (float) (-forward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if (sideward != 0) {
            destination.increaseRelativePos((float) (sideward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y))), 0, (float) (sideward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))));
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
    public static final void moveXYZ(GameObject source, GameObject destination, float forward, float sideward, float upward) {
        if (forward != 0) {
            destination.increaseRelativePos((float) (forward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))), (float) (-forward * Math.sin(Math.toRadians(source.getAbsoluteRotation().x))), (float) (-forward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().x))));
        }
        if (sideward != 0) {
            destination.increaseRelativePos((float) (sideward * Math.cos(Math.toRadians(source.getAbsoluteRotation().y)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().z))), (float) (-sideward * Math.sin(Math.toRadians(source.getAbsoluteRotation().z))), (float) (sideward * Math.sin(Math.toRadians(source.getAbsoluteRotation().y))));
        }
        if (upward != 0) {
            destination.increaseRelativePos((float) (upward * Math.sin(Math.toRadians(source.getAbsoluteRotation().z))), (float) (upward * Math.cos(Math.toRadians(source.getAbsoluteRotation().x)) * Math.cos(Math.toRadians(source.getAbsoluteRotation().z))), (float) (-upward * Math.sin(Math.toRadians(source.getAbsoluteRotation().x))));
        }
    }
    
    public static final void rotateNormal(final GameObject destination, final float deltaRotX, final float deltaRotY, final float deltaRotZ) {
        if(destination == null) {
            return;
        }
        rotateNormal(destination, destination, deltaRotX, deltaRotY, deltaRotZ);
    }
    
    public static final void rotateNormal(final GameObject source, final GameObject destination, final float deltaRotX, final float deltaRotY, final float deltaRotZ) {
        if(destination == null) {
            return;
        }
        final AxisAngle4f x_axis = new AxisAngle4f((float) Math.toRadians(deltaRotX), Maths.X);
        final AxisAngle4f y_axis = new AxisAngle4f((float) Math.toRadians(deltaRotY), Maths.Y);
        final AxisAngle4f z_axis = new AxisAngle4f((float) Math.toRadians(deltaRotZ), Maths.Z);
        if(source != null) {
            final Vector3f rotation = source.getAbsoluteRotation();
            final Matrix3f rotationMatrix = new Matrix3f();
            rotationMatrix.rotate((float) Math.toRadians(rotation.x), Maths.X);
            rotationMatrix.rotate((float) Math.toRadians(rotation.y), Maths.Y);
            rotationMatrix.rotate((float) Math.toRadians(rotation.z), Maths.Z);
            rotationMatrix.getEulerAnglesZYX(rotation);
            x_axis.set(x_axis.angle, Maths.X.mul(rotationMatrix, new Vector3f()));
            y_axis.set(y_axis.angle, Maths.Y.mul(rotationMatrix, new Vector3f()));
            z_axis.set(z_axis.angle, Maths.Z.mul(rotationMatrix, new Vector3f()));
            Logger.log("\nMaths.X == " + Maths.X + ", Maths.X Rotated == " + x_axis.toString() +
                       "\nMaths.Y == " + Maths.Y + ", Maths.Y Rotated == " + y_axis.toString() +
                       "\nMaths.Z == " + Maths.Z + ", Maths.Z Rotated == " + z_axis.toString());
        }
        //rotateNormal(destination, x_axis, y_axis, z_axis);
    }
    
    public static final void rotateNormal(final GameObject destination, final AxisAngle4f x_axis, final AxisAngle4f y_axis, final AxisAngle4f z_axis) {
        if(destination == null) {
            return;
        }
        final Vector3f rotation = destination.getRelativeRotation();
        final Matrix3f rotationMatrix = new Matrix3f();
        rotationMatrix.rotate(x_axis);
        rotation.mul(rotationMatrix);
        rotationMatrix.identity();
        rotationMatrix.rotate(x_axis);
        rotation.mul(rotationMatrix);
        rotationMatrix.identity();
        rotationMatrix.rotate(z_axis);
        rotation.mul(rotationMatrix);
        rotationMatrix.identity();
    }

    public static final void turnXZ(GameObject source, GameObject destination, float deltaRotX, float deltaRotY, float deltaRotZ) {
        final Vector3f rotation = new Vector3f(source.getAbsoluteRotation());
        if (deltaRotZ != 0) {
            destination.increaseRelativeRot((float) (-deltaRotZ * Math.sin(Math.toRadians(rotation.y))), 0, (float) (deltaRotZ * Math.cos(Math.toRadians(rotation.y))));
        }
        if (deltaRotX != 0) {
            destination.increaseRelativeRot((float) (-deltaRotX * Math.cos(Math.toRadians(rotation.y))), 0, (float) (deltaRotX * Math.sin(Math.toRadians(rotation.y))));
        }
        if (deltaRotY != 0) {
            destination.increaseRelativeRot(0, deltaRotY, 0);
        }
    }

    public static final void turnXZY(GameObject source, GameObject destination, float deltaRotX, float deltaRotY, float deltaRotZ) {
        final Vector3f rotation = new Vector3f(source.getAbsoluteRotation());
        if (deltaRotZ != 0) {
            destination.increaseRelativeRot((float) (-deltaRotZ * Math.sin(Math.toRadians(rotation.y))), (float) (deltaRotZ * Math.sin(Math.toRadians(rotation.x))), (float) (deltaRotZ * Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))));
        }
        if (deltaRotX != 0) {
            destination.increaseRelativeRot((float) (deltaRotX * Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.z))), (float) (-deltaRotX * Math.sin(Math.toRadians(rotation.z))), (float) (deltaRotX * Math.sin(Math.toRadians(rotation.y))));
        }
        if (deltaRotY != 0) {
            destination.increaseRelativeRot(0, deltaRotY, 0);
        }
    }

    public static final void turnXYZ(GameObject source, GameObject destination, float deltaRotX, float deltaRotY, float deltaRotZ) {
        final Vector3f rotation = new Vector3f(source.getAbsoluteRotation());
        if (deltaRotZ != 0) {
            destination.increaseRelativeRot((float) (-deltaRotZ * Math.sin(Math.toRadians(rotation.y))), (float) (deltaRotZ * Math.sin(Math.toRadians(rotation.x))), (float) (deltaRotZ * Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))));
        }
        if (deltaRotX != 0) {
            destination.increaseRelativeRot((float) (deltaRotX * Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.z))), (float) (-deltaRotX * Math.sin(Math.toRadians(rotation.z))), (float) (deltaRotX * Math.sin(Math.toRadians(rotation.y))));
        }
        if (deltaRotY != 0) {
            destination.increaseRelativeRot((float) (deltaRotY * Math.sin(Math.toRadians(rotation.z))), (float) (deltaRotY * Math.cos(Math.toRadians(rotation.x)) * Math.cos(Math.toRadians(rotation.z))), (float) (-deltaRotY * Math.sin(Math.toRadians(rotation.x))));
        }
    }

    public static final void turnNormal(GameObject source, GameObject destination, float deltaRotX, float deltaRotY, float deltaRotZ) {
        destination.increaseRelativeRot(deltaRotX, deltaRotY, deltaRotZ);
    }

}
