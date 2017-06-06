package omnikryptec.settings;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;

/**
 * Key settings objcet
 * @author Panzer1119
 */
public class KeySettings {
    
    /**
     * Standard KeySettings
     */
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
    
    /**
     * Constructs a KeySettings object
     * @param ikeys IKey Array IKeys
     */
    public KeySettings(IKey... ikeys) {
        initKeys();
        setKeys(ikeys);
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
    
    /**
     * Returns the IKeys
     * @return ArrayList IKey IKeys
     */
    public final ArrayList<IKey> getKeys() {
        return keys;
    }
    
    /**
     * Sets/Adds IKeys
     * @param keys IKey Array IKeys
     * @return KeySettings A reference to this KeySettings
     */
    public final KeySettings setKeys(IKey... keys) {
        for(IKey key : keys) {
            setKey(key);
        }
        return this;
    }
    
    /**
     * Returns if a IKey given by the name is pressed and no other IKey with these keys is pressed
     * @param name String Name of the IKey
     * @return <tt>true</tt> if the IKey is pressed and the only one pressed with its keys
     */
    public final boolean isPressed(String name) {
        IKey ikey = getKey(name);
        if(ikey == null || ikey == Key.DEFAULT_NULL_KEY) {
            ikey = getKeyGroup(name);
            if(ikey == null || ikey == KeyGroup.DEFAULT_NULL_KEYGROUP) {
                return false;
            } else {
                final KeyGroup keyGroup = (KeyGroup) ikey;
                if(!keyGroup.isPressed()) {
                    return false;
                }
                final ArrayList<IKey> temp = keyGroup.getKeys();
                final ArrayList<IKey> ikeys = getIKeys(temp);
                for(IKey ikey_ : ikeys) {
                    if(ikey_ instanceof KeyGroup) {
                        final KeyGroup keyGroup_ = (KeyGroup) ikey_;
                        if(keyGroup_.isPressed() && keyGroup_.getKeys().size() > temp.size() && !keyGroup_.getKeys().equals(temp)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        } else {
            final Key key = (Key) ikey;
            if(!key.isPressed()) {
                return false;
            }
            final ArrayList<Key> temp = new ArrayList<>();
            temp.add(key);
            final ArrayList<KeyGroup> keyGroups = getKeyGroups(temp);
            return keyGroups.isEmpty();
        }
    }
    
    /**
     * Returns if a IKey given by the name is pressed for a specified time and no other IKey with these keys is pressed
     * @param name String Name of the IKey
     * @param minTime Float Minimum pressing time
     * @param maxTime Float Maximum pressing time
     * @return <tt>true</tt> if the IKey is pressed for the specified time and the only one pressed with its keys
     */
    public final boolean isLongPressed(String name, float minTime, float maxTime) {
        IKey ikey = getKey(name);
        if(ikey == null || ikey == Key.DEFAULT_NULL_KEY) {
            ikey = getKeyGroup(name);
            if(ikey == null || ikey == KeyGroup.DEFAULT_NULL_KEYGROUP) {
                return false;
            } else {
                final KeyGroup keyGroup = (KeyGroup) ikey;
                if(!keyGroup.isLongPressed(minTime, maxTime)) {
                    return false;
                }
                final ArrayList<IKey> temp = keyGroup.getKeys();
                final ArrayList<IKey> ikeys = getIKeys(temp);
                for(IKey ikey_ : ikeys) {
                    if(ikey_ instanceof KeyGroup) {
                        final KeyGroup keyGroup_ = (KeyGroup) ikey_;
                        if(keyGroup_.isPressed() && keyGroup_.getKeys().size() > temp.size() && !keyGroup_.getKeys().equals(temp)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        } else {
            final Key key = (Key) ikey;
            if(!key.isLongPressed(minTime, maxTime)) {
                return false;
            }
            final ArrayList<Key> temp = new ArrayList<>();
            temp.add(key);
            final ArrayList<KeyGroup> keyGroups = getKeyGroups(temp);
            return keyGroups.isEmpty();
        }
    }
    
    /**
     * Sets/Adds Key
     * @param name String Name
     * @param key Integer Keyboard/Mouse key reference
     * @param isKeyboardKey Boolean If the key is a keyboard key
     * @return KeySettings A reference to this KeySettings
     */
    public final KeySettings setKey(String name, int key, boolean isKeyboardKey) {
        return setKey(new Key(name, key, isKeyboardKey));
    }
    
    /**
     * Sets/Adds IKey
     * @param key IKey IKey
     * @return KeySettings A reference to this KeySettings
     */
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
    
    
    /**
     * Updates all Keys given by the key and the state
     * @param currentTime Float Current time
     * @param key Integer Keyboard/Mouse key reference
     * @param isKeyboardKey Boolean If the key is a keyboard key
     * @return KeySettings A reference to this KeySettings
     */
    public final KeySettings updateKeys(float currentTime, int key, boolean isKeyboardKey) {
        return updateKeys(keys, currentTime, key, isKeyboardKey);
    }
    
    /**
     * Updates all Keys given by the key and the state
     * @param ikeys ArrayList IKey Keys to be updated
     * @param currentTime Float Current time
     * @param key Integer Keyboard/Mouse key reference
     * @param isKeyboardKey Boolean If the key is a keyboard key
     * @return KeySettings A reference to this KeySettings
     */
    public final KeySettings updateKeys(ArrayList<IKey> ikeys, float currentTime, int key, boolean isKeyboardKey) {
        for(IKey ikey : ikeys) {
            if(ikey instanceof Key) {
                final Key key_ = (Key) ikey;
                if(key_.getKey() == key && key_.isKeyboardKey() == isKeyboardKey) {
                    key_.setLastChange(currentTime);
                }
            } else if(ikey instanceof KeyGroup) {
                updateKeys(((KeyGroup) ikey).getKeys(), currentTime, key, isKeyboardKey);
            }
        }
        return this;
    }
    
    /**
     * Returns a Key given by the given name
     * @param name String Name
     * @return Key Key or DefaultNullKey
     */
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
    
    /**
     * Returns a Key given by the given key
     * @param key Integer Key
     * @param isKeyboardKey Boolean If the key is a keyboard key
     * @return Key Key or DefaultNullKey
     */
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
    
    /**
     * Returns all Keys that have the given key
     * @param key Integer Key
     * @param isKeyboardKey Boolean If the key is a keyboard key
     * @return ArrayList Key Keys
     */
    public final ArrayList<Key> getKeys(int key, boolean isKeyboardKey) {
        final ArrayList<Key> keys_ = new ArrayList<>();
        for(IKey key_temp : keys) {
            if(!(key_temp instanceof Key)) {
                continue;
            }
            final Key key_temp_ = (Key) key_temp;
            if(key_temp_.getKey() == key && key_temp_.isKeyboardKey() == isKeyboardKey) {
                keys_.add(key_temp_);
            }
        }
        return keys_;
    }
    
    /**
     * Returns a KeyGroup by the given name
     * @param name String Name
     * @return KeyGroup KeyGroup of DefaultNullKeyGroup
     */
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
    
    /**
     * Returns all KeyGroups that contains the given Keys
     * @param keys_ ArrayList Key Keys
     * @return ArrayList KeyGroup KeyGroups
     */
    public final ArrayList<KeyGroup> getKeyGroups(ArrayList<Key> keys_) {
        final ArrayList<KeyGroup> keyGroups_ = new ArrayList<>();
        for(IKey keyGroup_temp : keys) {
            if(!(keyGroup_temp instanceof KeyGroup)) {
                continue;
            }
            final KeyGroup keyGroup_temp_ = (KeyGroup) keyGroup_temp;
            if(keyGroup_temp_.getKeys().containsAll(keys_)) {
                keyGroups_.add(keyGroup_temp_);
            }
        }
        return keyGroups_;
    }
    
    /**
     * Returns all IKeys that contains the given IKeys
     * @param ikeys_ ArrayList IKey IKeys
     * @return ArrayList IKey IKeys
     */
    public final ArrayList<IKey> getIKeys(ArrayList<IKey> ikeys_) {
        final ArrayList<IKey> ikeys = new ArrayList<>();
        for(IKey ikey : keys) {
            if(ikey instanceof KeyGroup) {
                if(((KeyGroup) ikey).getKeys().containsAll(ikeys_)) {
                    ikeys.add(ikey);
                }
            } else if(ikey instanceof Key) {
                if(ikeys_.contains(ikey)) {
                    ikeys.add(ikey);
                }
            }
        }
        return ikeys;
    }
    
    /**
     * Removes a Key given by the key
     * @param key Integer Key
     * @param isKeyboardKey Boolean If the key is a keyboard key
     * @return KeySettings A reference to this KeySettings
     */
    public final KeySettings removeKey(int key, boolean isKeyboardKey) {
        final Key key_temp = getKey(key, isKeyboardKey);
        if(key_temp != null && key_temp != Key.DEFAULT_NULL_KEY) {
            removeKey(key_temp);
        }
        return this;
    }
    
    /**
     * Removes a Key given by the name
     * @param name String Name
     * @return KeySettings A reference to this KeySettings
     */
    public final KeySettings removeKey(String name) {
        return removeKey(new Key(name, -1, true));
    }
    
    /**
     * Removes a KeyGroup given by the name
     * @param name String Name
     * @return KeySettings A reference to this KeySettings
     */
    public final KeySettings removeKeyGroup(String name) {
        return removeKey(new KeyGroup(name));
    }
    
    /**
     * Removes an IKey
     * @param key IKey IKey
     * @return KeySettings A reference to this KeySettings
     */
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

}
