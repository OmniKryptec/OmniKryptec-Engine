package omnikryptec.util;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Panzer1119
 */
public class SerializationUtil {
    
    public static final String MATRIX_SPLITTER = ",";
    public static final String VECTOR_SPLITTER = ",";
    
    public static final String matrix3fToString(Matrix3f matrix) {
        if(matrix == null) {
            return null;
        }
        return matrix.m00 + MATRIX_SPLITTER + matrix.m01 + MATRIX_SPLITTER + matrix.m02 + MATRIX_SPLITTER +
               matrix.m10 + MATRIX_SPLITTER + matrix.m11 + MATRIX_SPLITTER + matrix.m12 + MATRIX_SPLITTER +
               matrix.m20 + MATRIX_SPLITTER + matrix.m21 + MATRIX_SPLITTER + matrix.m22;
    }
    
    public static final Matrix3f stringToMatrix3f(String temp) {
        if(temp == null) {
            return null;
        }
        final String[] split = temp.split(MATRIX_SPLITTER);
        if(split.length != 9) {
            return null;
        }
        final Matrix3f matrix = new Matrix3f();
        matrix.m00 = Float.parseFloat(split[0]);
        matrix.m01 = Float.parseFloat(split[1]);
        matrix.m02 = Float.parseFloat(split[2]);
        matrix.m10 = Float.parseFloat(split[3]);
        matrix.m11 = Float.parseFloat(split[4]);
        matrix.m12 = Float.parseFloat(split[5]);
        matrix.m20 = Float.parseFloat(split[6]);
        matrix.m21 = Float.parseFloat(split[7]);
        matrix.m22 = Float.parseFloat(split[8]);
        return matrix;
    }
    
    public static final String matrix4fToString(Matrix4f matrix) {
        if(matrix == null) {
            return null;
        }
        return matrix.m00 + MATRIX_SPLITTER + matrix.m01 + MATRIX_SPLITTER + matrix.m02 + MATRIX_SPLITTER + matrix.m03 + MATRIX_SPLITTER +
               matrix.m10 + MATRIX_SPLITTER + matrix.m11 + MATRIX_SPLITTER + matrix.m12 + MATRIX_SPLITTER + matrix.m13 + MATRIX_SPLITTER +
               matrix.m20 + MATRIX_SPLITTER + matrix.m21 + MATRIX_SPLITTER + matrix.m22 + MATRIX_SPLITTER + matrix.m23 + MATRIX_SPLITTER +
               matrix.m30 + MATRIX_SPLITTER + matrix.m31 + MATRIX_SPLITTER + matrix.m32 + MATRIX_SPLITTER + matrix.m33;
    }
    
    public static final Matrix4f stringToMatrix4f(String temp) {
        if(temp == null) {
            return null;
        }
        final String[] split = temp.split(MATRIX_SPLITTER);
        if(split.length != 16) {
            return null;
        }
        final Matrix4f matrix = new Matrix4f();
        matrix.m00 = Float.parseFloat(split[0]);
        matrix.m01 = Float.parseFloat(split[1]);
        matrix.m02 = Float.parseFloat(split[2]);
        matrix.m03 = Float.parseFloat(split[3]);
        matrix.m10 = Float.parseFloat(split[4]);
        matrix.m11 = Float.parseFloat(split[5]);
        matrix.m12 = Float.parseFloat(split[6]);
        matrix.m13 = Float.parseFloat(split[7]);
        matrix.m20 = Float.parseFloat(split[8]);
        matrix.m21 = Float.parseFloat(split[9]);
        matrix.m22 = Float.parseFloat(split[10]);
        matrix.m23 = Float.parseFloat(split[11]);
        matrix.m30 = Float.parseFloat(split[12]);
        matrix.m31 = Float.parseFloat(split[13]);
        matrix.m32 = Float.parseFloat(split[14]);
        matrix.m33 = Float.parseFloat(split[15]);
        return matrix;
    }
    
    public static final String vector2fToString(Vector2f vector) {
        if(vector == null) {
            return null;
        }
        return vector.toString();
    }
    
    public static final Vector2f stringToVector2f(String temp) {
        if(temp == null || temp.length() < 10) {
            return null;
        }
        temp = temp.substring(9, temp.length() - 1).replaceAll(" ", "");
        final String[] split = temp.split(VECTOR_SPLITTER);
        return new Vector2f(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
    }
    
    public static final String vector3fToString(Vector3f vector) {
        if(vector == null) {
            return null;
        }
        return vector.toString();
    }
    
    public static final Vector3f stringToVector3f(String temp) {
        if(temp == null || temp.length() < 10) {
            return null;
        }
        temp = temp.substring(9, temp.length() - 1).replaceAll(" ", "");
        final String[] split = temp.split(VECTOR_SPLITTER);
        return new Vector3f(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
    }
    
}
