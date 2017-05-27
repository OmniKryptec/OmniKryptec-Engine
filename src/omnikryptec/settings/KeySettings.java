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
    
    public final KeySettings setKey(Key key) {
        return setKey(key.getName(), key.getKey(), key.isKeyboardKey());
    }
    
    public final KeySettings setKey(String name, int key, boolean isKeyboardKey) {
        final Key key_temp = getKey(name);
        if(key_temp != null) {
            key_temp.setKey(key);
            key_temp.setIsKeyboardKey(isKeyboardKey);
        } else {
            keys.add(new Key(name, key, isKeyboardKey));
        }
        return this;
    }
    
    public final Key getKey(String name) {
        for(Key key : keys) {
            if(key.getName().equals(name)) {
                return key;
            }
        }
        return null;
    }
    
    public final Key getKey(int key, boolean isKeyboardKey) {
        for(Key key_temp : keys) {
            if(key_temp.getKey() == key && key_temp.isKeyboardKey() == isKeyboardKey) {
                return key_temp;
            }
        }
        return null;
    }

}
