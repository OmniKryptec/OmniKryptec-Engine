package omnikryptec.settings;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Panzer1119
 */
public class KeyGroup implements IKey {
    
    public static final KeyGroup DEFAULT_NULL_KEYGROUP = new KeyGroup("DEFAULT_NULL_KEYGROUP");
    
    private final String name;
    private final ArrayList<IKey> keys = new ArrayList<>();
    
    public KeyGroup(String name, Key... keys) {
        this.name = name;
        addKeys(keys);
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final ArrayList<IKey> getKeys() {
        return keys;
    }
    
    public final KeyGroup addKey(Key key) {
        this.keys.add(key);
        return this;
    }
    
    public final KeyGroup addKeys(IKey[] keys) {
        if(keys == null || keys.length == 0) {
            return this;
        }
        for(IKey key : keys) {
            this.keys.add(key);
        }
        return this;
    }
    
    public final KeyGroup addKeys(ArrayList<IKey> keys) {
        if(keys == null || keys.isEmpty()) {
            return this;
        }
        for(IKey key : keys) {
            this.keys.add(key);
        }
        return this;
    }
    
    @Override
    public final boolean isPressed() {
        if(keys.isEmpty()) {
            return false;
        }
        boolean isPressed = true;
        for(IKey key : keys) {
            if(!key.isPressed()) {
                isPressed = false;
                break;
            }
        }
        return isPressed;
    }
    
    @Override
    public final boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o instanceof KeyGroup) {
            final KeyGroup keyGroup_temp = (KeyGroup) o;
            return keyGroup_temp.name.equals(name);
        } else {
            return false;
        }
    }
    
    @Override
    public final String toString() {
        return String.format("KeyGroup: \"%s\" (%d): %s", name, keys.size(), Arrays.toString(keys.toArray()));
    }
    
}
