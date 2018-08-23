package de.omnikryptec.test.saving;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataMap
 *
 * @author Panzer1119
 */
public class DataMap extends HashMap<String, Object> {

    private final String name;

    public DataMap(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final <E> List<E> getList(String name, Class<? extends E> type) {
        if (name == null || type == null) {
            return null;
        }
        final Object object = get(name);
        if (object == null) {
            return null;
        }
        return (List<E>) (List<?>) object;
    }

    public final <K, V> Map<K, V> getMap(String name, Class<? extends K> typeKey, Class<? extends V> typeValue) {
        if (name == null || typeKey == null || typeValue == null) {
            return null;
        }
        final Object object = get(name);
        if (object == null) {
            return null;
        }
        return (Map<K, V>) (Map<?, ?>) object;
    }

    public final DataMap getDataMap(String name) {
        if (name == null) {
            return null;
        }
        final Object object = get(name);
        if (object == null || !(object instanceof DataMap)) {
            return null;
        }
        return (DataMap) object;
    }

    public final String getString(String name) {
        if (name == null) {
            return null;
        }
        final Object object = get(name);
        if (object == null) {
            return null;
        }
        return ("" + object);
    }

    public final byte getByte(String name) {
        if (name == null) {
            return 0;
        }
        final Object object = get(name);
        if (object == null) {
            return 0;
        }
        return Byte.parseByte("" + object);
    }

    public final short getShort(String name) {
        if (name == null) {
            return 0;
        }
        final Object object = get(name);
        if (object == null) {
            return 0;
        }
        return Short.parseShort("" + object);
    }

    public final int getInt(String name) {
        if (name == null) {
            return 0;
        }
        final Object object = get(name);
        if (object == null) {
            return 0;
        }
        return Integer.parseInt("" + object);
    }

    public final long getLong(String name) {
        if (name == null) {
            return 0;
        }
        final Object object = get(name);
        if (object == null) {
            return 0;
        }
        return Long.parseLong("" + object);
    }

    public final float getFloat(String name) {
        if (name == null) {
            return 0;
        }
        final Object object = get(name);
        if (object == null) {
            return 0;
        }
        return Float.parseFloat("" + object);
    }

    public final double getDouble(String name) {
        if (name == null) {
            return 0;
        }
        final Object object = get(name);
        if (object == null) {
            return 0;
        }
        return Double.parseDouble("" + object);
    }

    public final char getChar(String name) {
        if (name == null) {
            return 0;
        }
        final Object object = get(name);
        if (object == null && !("" + object).isEmpty()) {
            return 0;
        }
        return ("" + object).charAt(0);
    }

    public final boolean getBoolean(String name) {
        if (name == null) {
            return false;
        }
        final Object object = get(name);
        if (object == null) {
            return false;
        }
        return Boolean.parseBoolean("" + object);
    }

}
