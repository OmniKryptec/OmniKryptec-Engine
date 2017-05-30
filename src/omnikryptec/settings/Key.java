package omnikryptec.settings;

import omnikryptec.display.DisplayManager;
import omnikryptec.util.InputUtil;

/**
 *
 * @author Panzer1119
 */
public class Key implements IKey {
    
    public static final Key DEFAULT_NULL_KEY = new Key("DEFAULT_NULL_KEY", -1, true);
    
    private final String name;
    private int key = -1;
    private boolean isKeyboardKey = true;
    private float lastChange = 0.0F;
    
    public Key(String name, int key, boolean isKeyboardKey) {
        this.name = name;
        this.key = key;
        this.isKeyboardKey = isKeyboardKey;
    }

    @Override
    public final String getName() {
        return name;
    }
    
    public Key setKey(int key) {
        this.key = key;
        return this;
    }

    public final int getKey() {
        return key;
    }
    
    public final Key setIsKeyboardKey(boolean isKeyboardKey) {
        this.isKeyboardKey = isKeyboardKey;
        return this;
    }

    public final float getLastChange() {
        return lastChange;
    }

    public final Key setLastChange(float lastChange) {
        this.lastChange = lastChange;
        return this;
    }
    
    @Override
    public final boolean isPressed() {
        if(isKeyboardKey) {
            return InputUtil.isKeyboardKeyDown(key);
        } else {
            return InputUtil.isMouseKeyDown(key);
        }
    }
    
    /**
     * Returns if the Key is pressed longer then the given time
     * @param time Float Presstime of the Key in ms
     * @return Boolean If the Key is pressed for the given time
     */
    @Override
    public final boolean isLongPressed(float time) {
        final float currentTime = DisplayManager.instance().getCurrentTime();
        final boolean isLongPressed = isPressed() && ((currentTime - lastChange) > time);
        lastChange = currentTime;
        return isLongPressed;
    }
    
    public final boolean isKeyboardKey() {
        return isKeyboardKey;
    }
    
    @Override
    public final boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o instanceof Key) {
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
