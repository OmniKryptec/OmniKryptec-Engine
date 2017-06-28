package omnikryptec.settings;

import omnikryptec.display.DisplayManager;
import omnikryptec.input.InputHandler;

/**
 * Key
 * 
 * @author Panzer1119
 */
public class Key implements IKey {

	/**
	 * Default Key which gets returned instead of null
	 */
	public static final Key DEFAULT_NULL_KEY = new Key("DEFAULT_NULL_KEY", -1, true);

	private final String name;
	private int key = -1;
	private boolean isKeyboardKey = true;
	private float lastChange = 0.0F;

	/**
	 * Constructs a key
	 * 
	 * @param name
	 *            String Name
	 * @param key
	 *            Integer Key
	 * @param isKeyboardKey
	 *            Boolean If the key is a keyboard key
	 */
	public Key(String name, int key, boolean isKeyboardKey) {
		this.name = name;
		this.key = key;
		this.isKeyboardKey = isKeyboardKey;
	}

	@Override
	public final String getName() {
		return name;
	}

	/**
	 * Sets the key
	 * 
	 * @param key
	 *            Integer Keyboard/Mouse key reference
	 * @return Key A reference to this Key
	 */
	public final Key setKey(int key) {
		this.key = key;
		return this;
	}

	/**
	 * Returns the key
	 * 
	 * @return Integer Keyboard/Mouse key reference
	 */
	public final int getKey() {
		return key;
	}

	/**
	 * Sets if this Key is a keyboard key
	 * 
	 * @param isKeyboardKey
	 *            Boolean If the key is a keyboard key
	 * @return Key A reference to this Key
	 */
	public final Key setIsKeyboardKey(boolean isKeyboardKey) {
		this.isKeyboardKey = isKeyboardKey;
		return this;
	}

	/**
	 * Returns if the key is a keyboard key
	 * 
	 * @return <tt>true</tt> if the key is a keyboard key
	 */
	public final boolean isKeyboardKey() {
		return isKeyboardKey;
	}

	/**
	 * Returns the last change of this Key
	 * 
	 * @return Float Last change
	 */
	public final float getLastChange() {
		return lastChange;
	}

	/**
	 * Sets the last change of this Key
	 * 
	 * @param lastChange
	 *            Float Last change
	 * @return Key A reference to this Key
	 */
	public final Key setLastChange(float lastChange) {
		this.lastChange = lastChange;
		return this;
	}

	@Override
	public final boolean isPressed() {
		if (isKeyboardKey) {
			return InputHandler.isKeyboardKeyDown(key);
		} else {
			return InputHandler.isMouseKeyDown(key);
		}
	}

	@Override
	public boolean isLongPressed(float minTime, float maxTime) {
		final float currentTime = DisplayManager.instance().getCurrentTime();
		final float pressedTime = (currentTime - lastChange);
		final boolean isLongPressed = isPressed() && (pressedTime >= minTime && pressedTime <= maxTime);
		if (isLongPressed) {
			lastChange = currentTime;
		}
		return isLongPressed;
	}

	@Override
	public final boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof Key) {
			final Key key_temp = (Key) o;
			return key_temp.name.equals(name);
		} else {
			return false;
		}
	}

	@Override
	public final String toString() {
		return String.format("Key: \"%s\" == %d, isKeyboardKey: %b", name, key, isKeyboardKey);
	}

}
