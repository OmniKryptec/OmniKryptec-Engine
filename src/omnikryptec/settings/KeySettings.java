package omnikryptec.settings;

import org.lwjgl.input.Keyboard;

/**
 *
 * @author Panzer1119
 */
public class KeySettings {
    
    public static final KeySettings STANDARDKEYSETTINGS = new KeySettings(Keyboard.KEY_W, Keyboard.KEY_S, Keyboard.KEY_D, Keyboard.KEY_A, Keyboard.KEY_SPACE, Keyboard.KEY_LSHIFT, Keyboard.KEY_RIGHT, Keyboard.KEY_LEFT, Keyboard.KEY_UP, Keyboard.KEY_DOWN, Keyboard.KEY_E, Keyboard.KEY_Q);
    
    private int moveForward;
    private int moveBackward;
    private int moveRight;
    private int moveLeft;
    private int moveUp;
    private int moveDown;
    private int turnYawRight;
    private int turnYawLeft;
    private int turnPitchUp;
    private int turnPitchDown;
    private int turnRollRight;
    private int turnRollLeft;
    
    public KeySettings() {
    }

    public KeySettings(int moveForward, int moveBackward, int moveRight, int moveLeft, int moveUp, int moveDown, int turnYawRight, int turnYawLeft, int turnPitchUp, int turnPitchDown, int turnRollRight, int turnRollLeft) {
        this.moveForward = moveForward;
        this.moveBackward = moveBackward;
        this.moveRight = moveRight;
        this.moveLeft = moveLeft;
        this.moveUp = moveUp;
        this.moveDown = moveDown;
        this.turnYawRight = turnYawRight;
        this.turnYawLeft = turnYawLeft;
        this.turnPitchUp = turnPitchUp;
        this.turnPitchDown = turnPitchDown;
        this.turnRollRight = turnRollRight;
        this.turnRollLeft = turnRollLeft;
    }

    public int getMoveForward() {
        return moveForward;
    }

    public KeySettings setMoveForward(int moveForward) {
        this.moveForward = moveForward;
        return this;
    }

    public int getMoveBackward() {
        return moveBackward;
    }

    public KeySettings setMoveBackward(int moveBackward) {
        this.moveBackward = moveBackward;
        return this;
    }

    public int getMoveRight() {
        return moveRight;
    }

    public KeySettings setMoveRight(int moveRight) {
        this.moveRight = moveRight;
        return this;
    }

    public int getMoveLeft() {
        return moveLeft;
    }

    public KeySettings setMoveLeft(int moveLeft) {
        this.moveLeft = moveLeft;
        return this;
    }

    public int getMoveUp() {
        return moveUp;
    }

    public KeySettings setMoveUp(int moveUp) {
        this.moveUp = moveUp;
        return this;
    }

    public int getMoveDown() {
        return moveDown;
    }

    public KeySettings setMoveDown(int moveDown) {
        this.moveDown = moveDown;
        return this;
    }

    public int getTurnYawRight() {
        return turnYawRight;
    }

    public KeySettings setTurnYawRight(int turnYawRight) {
        this.turnYawRight = turnYawRight;
        return this;
    }

    public int getTurnYawLeft() {
        return turnYawLeft;
    }

    public KeySettings setTurnYawLeft(int turnYawLeft) {
        this.turnYawLeft = turnYawLeft;
        return this;
    }

    public int getTurnPitchUp() {
        return turnPitchUp;
    }

    public KeySettings setTurnPitchUp(int turnPitchUp) {
        this.turnPitchUp = turnPitchUp;
        return this;
    }

    public int getTurnPitchDown() {
        return turnPitchDown;
    }

    public KeySettings setTurnPitchDown(int turnPitchDown) {
        this.turnPitchDown = turnPitchDown;
        return this;
    }

    public int getTurnRollRight() {
        return turnRollRight;
    }

    public KeySettings setTurnRollRight(int turnRollRight) {
        this.turnRollRight = turnRollRight;
        return this;
    }

    public int getTurnRollLeft() {
        return turnRollLeft;
    }

    public KeySettings setTurnRollLeft(int turnRollLeft) {
        this.turnRollLeft = turnRollLeft;
        return this;
    }
    
}
