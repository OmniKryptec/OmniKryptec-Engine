package omnikryptec.settings;

import omnikryptec.util.InputUtil;

/**
 *
 * @author Panzer1119
 */
public class Key {
    
    private final String name;
    private int key = -1;
    private boolean isKeyboardKey = true;
    
    public Key(String name, int key, boolean isKeyboardKey) {
        this.name = name;
        this.key = key;
        this.isKeyboardKey = isKeyboardKey;
    }

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
    
    public Key setIsKeyboardKey(boolean isKeyboardKey) {
        this.isKeyboardKey = isKeyboardKey;
        return this;
    }
    
    public boolean isPressed() {
        if(isKeyboardKey) {
            return InputUtil.isKeyboardKeyDown(key);
        } else {
            return InputUtil.isMouseKeyDown(key);
        }
    }
    
    public final boolean isKeyboardKey() {
        return isKeyboardKey;
    }
    
    @Override
    public final String toString() {
        return String.format("Key: \"%s\" == %d, isKeyboardKey: %b", name, key, isKeyboardKey);
    }
    
}
