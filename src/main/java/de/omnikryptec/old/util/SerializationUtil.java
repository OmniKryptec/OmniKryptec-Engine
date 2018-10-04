/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import de.omnikryptec.old.gameobject.GameObject;
import de.omnikryptec.old.util.logger.Logger;
import de.omnikryptec.util.data.Color;

/**
 *
 * @author Panzer1119
 */
public class SerializationUtil {

    public static final String MATRIX_SPLITTER = ",";
    public static final String VECTOR_SPLITTER = ",";

    public static final String matrix3fToString(Matrix3f matrix) {
        if (matrix == null) {
            return null;
        }
        return matrix.m00 + MATRIX_SPLITTER + matrix.m01 + MATRIX_SPLITTER + matrix.m02 + MATRIX_SPLITTER + matrix.m10
                + MATRIX_SPLITTER + matrix.m11 + MATRIX_SPLITTER + matrix.m12 + MATRIX_SPLITTER + matrix.m20
                + MATRIX_SPLITTER + matrix.m21 + MATRIX_SPLITTER + matrix.m22;
    }

    public static final Matrix3f stringToMatrix3f(String temp) {
        if (temp == null) {
            return null;
        }
        final String[] split = temp.split(MATRIX_SPLITTER);
        if (split.length != 9) {
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

    public static final String matrix4fToString(Matrix4fc matrix) {
        if (matrix == null) {
            return null;
        }
        return matrix.m00() + MATRIX_SPLITTER + matrix.m01() + MATRIX_SPLITTER + matrix.m02() + MATRIX_SPLITTER + matrix.m03()
                + MATRIX_SPLITTER + matrix.m10() + MATRIX_SPLITTER + matrix.m11() + MATRIX_SPLITTER + matrix.m12()
                + MATRIX_SPLITTER + matrix.m13() + MATRIX_SPLITTER + matrix.m20() + MATRIX_SPLITTER + matrix.m21()
                + MATRIX_SPLITTER + matrix.m22() + MATRIX_SPLITTER + matrix.m23() + MATRIX_SPLITTER + matrix.m30()
                + MATRIX_SPLITTER + matrix.m31() + MATRIX_SPLITTER + matrix.m32() + MATRIX_SPLITTER + matrix.m33();
    }

    public static final Matrix4f stringToMatrix4f(String temp) {
        if (temp == null) {
            return null;
        }
        final String[] split = temp.split(MATRIX_SPLITTER);
        if (split.length != 16) {
            return null;
        }
        final Matrix4f matrix = new Matrix4f();
        matrix.m00( Float.parseFloat(split[0]));
        matrix.m01(  Float.parseFloat(split[1]));
        matrix.m02( Float.parseFloat(split[2]));
        matrix.m03 ( Float.parseFloat(split[3]));
        matrix.m10 ( Float.parseFloat(split[4]));
        matrix.m11 ( Float.parseFloat(split[5]));
        matrix.m12 ( Float.parseFloat(split[6]));
        matrix.m13 ( Float.parseFloat(split[7]));
        matrix.m20 ( Float.parseFloat(split[8]));
        matrix.m21 ( Float.parseFloat(split[9]));
        matrix.m22 ( Float.parseFloat(split[10]));
        matrix.m23 ( Float.parseFloat(split[11]));
        matrix.m30 ( Float.parseFloat(split[12]));
        matrix.m31 ( Float.parseFloat(split[13]));
        matrix.m32 (Float.parseFloat(split[14]));
        matrix.m33 ( Float.parseFloat(split[15]));
        return matrix;
    }

    public static final String vector2fToString(Vector2f vector) {
        if (vector == null) {
            return null;
        }
        return String.format(Locale.US, "[%f%s%f]", vector.x, VECTOR_SPLITTER, vector.y);
    }

    public static final Vector2f stringToVector2f(String temp) {
        if (temp == null || temp.length() < 10) {
            return null;
        }
        temp = temp.substring(1, temp.length() - 1).replaceAll(" ", "");
        final String[] split = temp.split(VECTOR_SPLITTER);
        return new Vector2f(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
    }

    public static final String vector3fToString(Vector3f vector) {
        if (vector == null) {
            return null;
        }
        return String.format(Locale.US, "[%f%s%f%s%f]", vector.x, VECTOR_SPLITTER, vector.y, VECTOR_SPLITTER, vector.z);
    }
    
    public static final Vector3f stringToVector3f(String temp) {
        if (temp == null || temp.length() < 10) {
            return null;
        }
        temp = temp.substring(1, temp.length() - 1).replaceAll(" ", "");
        final String[] split = temp.split(VECTOR_SPLITTER);
        return new Vector3f(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
    }
    
    public static final String vector4fToString(Vector4f vector) {
        if (vector == null) {
            return null;
        }
        return String.format(Locale.US, "[%f%s%f%s%f%s%f]", vector.x, VECTOR_SPLITTER, vector.y, VECTOR_SPLITTER, vector.z, VECTOR_SPLITTER, vector.w);
    }

    public static final Vector4f stringToVector4f(String temp) {
        if (temp == null || temp.length() < 10) {
            return null;
        }
        temp = temp.substring(1, temp.length() - 1).replaceAll(" ", "");
        final String[] split = temp.split(VECTOR_SPLITTER);
        return new Vector4f(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
    }

    public static final String quaternionfToString(Quaternionf rotation) {
        if (rotation == null) {
            return null;
        }
        return String.format(Locale.US, "[%f%s%f%s%f%s%f]", rotation.x, VECTOR_SPLITTER, rotation.y, VECTOR_SPLITTER, rotation.z, VECTOR_SPLITTER, rotation.w);
    }

    public static final Quaternionf stringToQuaternionf(String temp) {
        if (temp == null || temp.length() < 10) {
            return null;
        }
        temp = temp.substring(1, temp.length() - 1).replaceAll(" ", "");
        final String[] split = temp.split(VECTOR_SPLITTER);
        return new Quaternionf(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
    }
    
    public static final String colorToString(Color color) {
        if(color == null) {
            return null;
        }
        return vector4fToString(color.getVector4f());
    }
    
    public static final Color stringToColor(String temp) {
        final Vector4f color = stringToVector4f(temp);
        if(color == null) {
            return null;
        }
        return new Color(color);
    }
    
    public static final HashMap<Class<?>, ArrayList<GameObject>> gameObjectsToClassesGameObjects(ArrayList<GameObject> gameObjects) {
        final HashMap<Class<?>, ArrayList<GameObject>> classesGameObjects = new HashMap<>();
        for(GameObject gameObject : gameObjects) {
            final Class<?> c = gameObject.getClass();
            ArrayList<GameObject> gos = classesGameObjects.get(c);
            if(gos == null) {
                gos = new ArrayList<>();
                classesGameObjects.put(c, gos);
            }
            gos.add(gameObject);
        }
        return classesGameObjects;
    }

    public static final Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (Exception ex) {
            Logger.logErr("Failed to resolve class for \"" + className + "\": " + ex, ex);
            return null;
        }
    }

    public static final Object cast(Class<?> c, Object toCast) {
        if (toCast == null) {
            return null;
        }
        if (c == null) {
            return toCast;
        }
        if (toCast.getClass().isArray()) {
            return cast((Object[]) toCast, c);
        }
        if (toCast instanceof String) {
            String temp = (String) toCast;
            if (c == Long.class) {
                return Long.parseLong(temp);
            } else if (c == Float.class) {
                return Float.parseFloat(temp);
            } else if (c == Double.class) {
                return Double.parseDouble(temp);
            } else if (c == Integer.class) {
                return Integer.parseInt(temp);
            } else if (c == Short.class) {
                return Short.parseShort(temp);
            } else if (c == Boolean.class) {
                return Boolean.parseBoolean(temp);
            } else if (c == Byte.class) {
                return Byte.parseByte(temp);
            } else if (c == Character.class) {
                if (temp.length() >= 1) {
                    return temp.charAt(0);
                } else {
                    return null;
                }
            }
        }
        return c.cast(toCast);
    }

    public static final Object[] cast(Object[] toCast, Class<?>... c) {
        if (toCast == null) {
            return null;
        }
        if (c == null || toCast.length == 0) {
            return toCast;
        }
        for (int i = 0; i < toCast.length; i++) {
            toCast[i] = cast(c[i % c.length], toCast[i]);
        }
        return toCast;
    }

    
	public static final <T> T[] castArray(Object[] toCast, Class<? extends T> c) {
        if (toCast == null) {
            return null;
        }
        if (c == null || toCast.length == 0) {
            return (T[]) toCast;
        }
        T[] casted = (T[]) Array.newInstance(c, toCast.length);
        for (int i = 0; i < toCast.length; i++) {
            Object o = toCast[i];
            casted[i] = o.getClass().isArray() ? (T) castArray((Object[]) o, c) : (T) o;
        }
        return casted;
    }

    public static final Class<?> arrayClass(Class<?> c, int dimensions) {
        if (dimensions == 0) {
            return c;
        }
        int[] dims = new int[dimensions];
        return Array.newInstance(c, dims).getClass();
    }

    public static final Object makeArray(Class<?> c, int dimensions, int length) {
        return Array.newInstance(arrayClass(c, dimensions - 1), length);
    }

}
