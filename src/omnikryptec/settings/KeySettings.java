package omnikryptec.settings;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author Panzer1119
 */
public class KeySettings {
    
    public static final KeySettings STANDARDKEYSETTINGS = new KeySettings(new Key[] {new Key("moveForward", Keyboard.KEY_W, true), 
                                                                                     new Key("moveBackward", Keyboard.KEY_S, true), 
                                                                                     new Key("moveRight", Keyboard.KEY_D, true), 
                                                                                     new Key("moveLeft", Keyboard.KEY_A, true), 
                                                                                     new Key("moveUp", Keyboard.KEY_SPACE, true), 
                                                                                     new Key("moveDown", Keyboard.KEY_LSHIFT, true), 
                                                                                     new Key("turnYawRight", Keyboard.KEY_RIGHT, true), 
                                                                                     new Key("turnYawLeft", Keyboard.KEY_LEFT, true), 
                                                                                     new Key("turnPitchUp", Keyboard.KEY_UP, true), 
                                                                                     new Key("turnPitchDown", Keyboard.KEY_DOWN, true), 
                                                                                     new Key("turnRollRight", Keyboard.KEY_E, true), 
                                                                                     new Key("turnRollLeft", Keyboard.KEY_Q, true)});
    
    private final ArrayList<Key> keys = new ArrayList<>();
    
    public KeySettings(Key... keys) {
        initKeys();
        setKeys(keys);
    }
    
    private KeySettings initKeys() {
        keys.add(new Key("moveForward", -1, true));
        keys.add(new Key("moveBackward", -1, true));
        keys.add(new Key("moveRight", -1, true));
        keys.add(new Key("moveLeft", -1, true));
        keys.add(new Key("moveUp", -1, true));
        keys.add(new Key("moveDown", -1, true));
        keys.add(new Key("turnYawRight", -1, true));
        keys.add(new Key("turnYawLeft", -1, true));
        keys.add(new Key("turnPitchUp", -1, true));
        keys.add(new Key("turnPitchDown", -1, true));
        keys.add(new Key("turnRollRight", -1, true));
        keys.add(new Key("turnRollLeft", -1, true));
        return this;
    }
    
    public final ArrayList<Key> getKeys() {
        return keys;
    }
    
    public final KeySettings setKeys(Key... keys) {
        for(Key key : keys) {
            setKey(key);
        }
        return this;
    }
    
    public final KeySettings setKey(String name, int key, boolean isKeyboardKey) {
        return setKey(new Key(name, key, isKeyboardKey));
    }
    
    public final KeySettings setKey(Key key) {
        final int index = keys.indexOf(key);
        if(index >= 0) {
            keys.get(index).setKey(key.getKey());
            keys.get(index).setIsKeyboardKey(key.isKeyboardKey());
        } else {
            keys.add(key);
        }
        return this;
    }
    
    public final Key getKey(String name) {
        for(Key key : keys) {
            if(key.getName().equals(name)) {
                return key;
            }
        }
        return Key.DEFAULT_NULL_KEY;
    }
    
    public final Key getKey(int key, boolean isKeyboardKey) {
        for(Key key_temp : keys) {
            if(key_temp.getKey() == key && key_temp.isKeyboardKey() == isKeyboardKey) {
                return key_temp;
            }
        }
        return Key.DEFAULT_NULL_KEY;
    }
    
    public final KeySettings removeKey(int key, boolean isKeyboardKey) {
        final Key key_temp = getKey(key, isKeyboardKey);
        if(key_temp != Key.DEFAULT_NULL_KEY) {
            removeKey(key_temp);
        }
        return this;
    }
    
    public final KeySettings removeKey(String name) {
        return removeKey(new Key(name, -1, true));
    }
    
    public final KeySettings removeKey(Key key) {
        int index = -1;
        while((index = keys.indexOf(key)) != -1) {
            keys.remove(index);
        }
        return this;
    }

}
