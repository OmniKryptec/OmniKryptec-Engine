package de.omnikryptec.util.settings;

public class IntegerKey implements Defaultable {

    private final int value;
    private final Object defObj;

    public IntegerKey(final int value) {
        this(value, null);
    }

    public IntegerKey(final int value, final Object defObject) {
        this.value = value;
        this.defObj = defObject;
    }

    public int get() {
        return this.value;
    }

    @Override
    public <T> T getDefault() {
        return (T) this.defObj;
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof IntegerKey) {
            if (((IntegerKey) obj).value == this.value) {
                return true;
            }
        }
        return false;
    }
}
