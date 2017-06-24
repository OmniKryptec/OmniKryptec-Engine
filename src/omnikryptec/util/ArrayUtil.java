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
    
    public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        @SuppressWarnings("unchecked")
        T[] copy = ((Object)newType == (Object)Object[].class) ? (T[]) new Object[newLength] : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0, newLength);
        return copy;
    }
    
    public static <T> boolean contains(T[] array, T toTest) {
        if(array.length == 0 || toTest == null) {
            return false;
        }
        for(T t : array) {
            if(t == null) {
                continue;
            }
            if(t.equals(toTest) || t == toTest) {
                return true;
            }
        }
        return false;
    }
    
}
