package de.omnikryptec.util.settings;

public class IntegerKey implements Defaultable {
    
    private final int value;
    private final Object defObj;
    
    public IntegerKey(int value) {
        this(value, null);
    }
    
    public IntegerKey(int value, Object defObject) {
        this.value = value;
        this.defObj = defObject;
    }
    
    public int get() {
        return value;
    }
    
    @Override
    public <T> T getDefault() {
        return (T) defObj;
    }
    
    @Override
    public int hashCode() {
        return value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof IntegerKey) {
            if (((IntegerKey) obj).value == value) {
                return true;
            }
        }
        return false;
    }
}
