package omnikryptec.input;

import org.lwjgl.input.Keyboard;

/**
 * 
 * @author pcfreak9000
 *
 */
public class InputUtil {

	private InputUtil() {
	}

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

	private static boolean[] keys = new boolean[Keyboard.KEYBOARD_SIZE];

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
}
