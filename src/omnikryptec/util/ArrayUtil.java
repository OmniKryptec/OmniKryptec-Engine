package omnikryptec.util;

import java.lang.reflect.Array;

/**
 *
 * @author Panzer1119
 */
public class ArrayUtil {

    @SuppressWarnings("unchecked")
    public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[]) copyOf(original, newLength, original.getClass());
    }

    public static <T, U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        @SuppressWarnings("unchecked")
        T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength]
                : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0, newLength);
        return copy;
    }

    public static <T> boolean contains(T[] array, T toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return false;
        }
        for (T t : array) {
            if (t == null) {
                continue;
            }
            if (t.equals(toTest) || t == toTest) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean contains(T[] array, T... toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return false;
        }
        if(toTest.length == 0) {
            return true;
        }
        for (T t_1 : toTest) {
            if(t_1 == null) {
                continue;
            }
            boolean found = false;
            for(T t_2 : array) {
                if(t_2 == null) {
                    continue;
                }
                if(t_1.equals(t_2) || t_1 == t_2) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                return false;
            }
        }
        return true;
    }

}
