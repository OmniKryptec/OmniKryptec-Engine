package omnikryptec.settings;

import java.util.ArrayList;
import omnikryptec.logger.LogEntry;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;

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
    
    private final ArrayList<IKey> keys = new ArrayList<>();
    
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
    
    public final ArrayList<IKey> getKeys() {
        return keys;
    }
    
    public final KeySettings setKeys(IKey... keys) {
        for(IKey key : keys) {
            setKey(key);
        }
        return this;
    }
    
    public final KeySettings setKey(String name, int key, boolean isKeyboardKey) {
        return setKey(new Key(name, key, isKeyboardKey));
    }
    
    public final KeySettings setKey(IKey key) {
        final int index = keys.indexOf(key);
        if(index >= 0) {
            final IKey key_old = keys.get(index);
            if(key_old instanceof Key && key instanceof Key) {
                final Key key_old_ = (Key) key_old;
                final Key key_new_ = (Key) key;
                key_old_.setKey(key_new_.getKey());
                key_old_.setIsKeyboardKey(key_new_.isKeyboardKey());
            } else if(key_old instanceof KeyGroup && key instanceof KeyGroup) {
                final KeyGroup keyGroup_old_ = (KeyGroup) key_old;
                final KeyGroup keyGroup_new_ = (KeyGroup) key;
                keyGroup_old_.getKeys().clear();
                keyGroup_old_.addKeys(keyGroup_new_.getKeys());
            } else {
                keys.remove(key_old);
                keys.add(key);
                if(Logger.isDebugMode()) {
                    Logger.log("You replaced a Key with a KeyGroup or a KeyGroup with a Key", LogLevel.WARNING);
                }
            }
        } else {
            keys.add(key);
        }
        return this;
    }
    
    public final Key getKey(String name) {
        for(IKey key : keys) {
            if(!(key instanceof Key)) {
                continue;
            }
            if(key.getName().equals(name)) {
                return (Key) key;
            }
        }
        return Key.DEFAULT_NULL_KEY;
    }
    
    public final Key getKey(int key, boolean isKeyboardKey) {
        for(IKey key_temp : keys) {
            if(!(key_temp instanceof Key)) {
                continue;
            }
            final Key key_temp_ = (Key) key_temp;
            if(key_temp_.getKey() == key && key_temp_.isKeyboardKey() == isKeyboardKey) {
                return key_temp_;
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
    
    public final KeySettings removeKey(IKey key) {
        int index = -1;
        int lastIndex = -1;
        while(((index = keys.indexOf(key)) != -1) && (lastIndex != index)) {
            if(key.getClass() == keys.get(index).getClass()) {
                keys.remove(index);
            }
            lastIndex = index;
        }
        return this;
    }
    
    public final KeyGroup getKeyGroup(String name) {
        for(IKey key : keys) {
            if(!(key instanceof KeyGroup)) {
                continue;
            }
            final KeyGroup keyGroup_temp = (KeyGroup) key;
            if(keyGroup_temp.getName().equals(name)) {
                return keyGroup_temp;
            }
        }
        return KeyGroup.DEFAULT_NULL_KEYGROUP;
    }
    
    public final KeySettings removeKeyGroup(String name) {
        return removeKey(new KeyGroup(name));
    }

}
