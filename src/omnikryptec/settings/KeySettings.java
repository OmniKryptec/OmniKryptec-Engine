package omnikryptec.settings;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

/**
 *
 * @author Panzer1119
 */
public class KeySettings {
    
    public static final KeySettings STANDARDKEYSETTINGS = new KeySettings(new Key[] {new Key("mouseButtonLeft", 0, false),
                                                                                     new Key("mouseButtonRight", 1, false),
                                                                                     new Key("mouseButtonMiddle", 2, false),
                                                                                     new Key("moveForward", Keyboard.KEY_W, true), 
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
    private final ArrayList<KeyGroup> keyGroups = new ArrayList<>();
    
    public KeySettings(Key... keys) {
        initKeys();
        setKeys(keys);
    }
    
    private KeySettings initKeys() {
        keys.add(new Key("mouseButtonLeft", 0, false));
        keys.add(new Key("mouseButtonRight", 1, false));
        keys.add(new Key("mouseButtonMiddle", 2, false));
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
    
    public final KeySettings setKeyGroups(KeyGroup... keyGroups) {
        for(KeyGroup keyGroup : keyGroups) {
            setKeyGroup(keyGroup);
        }
        return this;
    }
    
    public final KeySettings setKeyGroup(KeyGroup keyGroup) {
        final int index = keyGroups.indexOf(keyGroup);
        if(index >= 0) {
            KeyGroup keyGroup_old = keyGroups.get(index);
            keyGroup_old.getKeys().clear();
            keyGroup_old.addKeys(keyGroup.getKeys());
        } else {
            keyGroups.add(keyGroup);
        }
        return this;
    }
    
    public final KeyGroup getKeyGroup(String name) {
        for(KeyGroup keyGroup : keyGroups) {
            if(keyGroup.getName().equals(name)) {
                return keyGroup;
            }
        }
        return KeyGroup.DEFAULT_NULL_KEYGROUP;
    }
    
    public final KeySettings removeKeyGroup(String name) {
        return removeKeyGroup(new KeyGroup(name));
    }
    
    public final KeySettings removeKeyGroup(KeyGroup keyGroup) {
        int index = -1;
        while((index = keyGroups.indexOf(keyGroup)) != -1) {
            keyGroups.remove(index);
        }
        return this;
    }

}
