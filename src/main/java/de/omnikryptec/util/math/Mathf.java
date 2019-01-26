/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.util.math;

import org.joml.Math;
import org.joml.Quaternionfc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

public strictfp class Mathf {
    
    public static final float PI = (float) java.lang.Math.PI;
    public static final float E = (float) java.lang.Math.E;
    
    /**
     * All float values above or equal to this value are integer numbers, all float
     * values below or equal to (-1) * this value are integer numbers.
     */
    private static final float TWO_POW_23 = 8388608.0f;
    
    public static float clamp(final float in, final float min, final float max) {
        return in < min ? min : (in > max ? max : in);
    }
    
    public static float clamp01(final float in) {
        return in < 0.0f ? 0.0f : (in > 1.0f ? 1.0f : in);
    }
    
    public static float sin(final float rad) {
        return (float) Math.sin(rad);
    }
    
    public static float cos(final float rad) {
        return (float) Math.cos(rad);
    }
    
    public static float tan(final float rad) {
        return (float) Math.tan(rad);
    }
    
    public static float arcsin(final float x) {
        return (float) Math.asin(x);
    }
    
    public static float arccos(final float x) {
        return (float) Math.acos(x);
    }
    
    public static float arctan(final float x) {
        return (float) java.lang.Math.atan(x);
    }
    
    public static float arctan2(final float y, final float x) {
        return (float) Math.atan2(y, x);
    }
    
    public static float sqrt(final float value) {
        return (float) Math.sqrt(value);
    }
    
    public static float abs(final float value) {
        return value < 0.0f ? 0.0f - value : value;
    }
    
    public static float min(final float v0, final float v1) {
        return v0 < v1 ? v0 : v1;
    }
    
    public static float max(final float v0, final float v1) {
        return v0 > v1 ? v0 : v1;
    }
    
    public static float floor(final float value) {
        if (value != value) {
            // NaN
            return value;
        }
        if (value >= TWO_POW_23 || value <= -TWO_POW_23) {
            return value;
        }
        int intvalue = (int) value;
        if (value < 0 && intvalue != value) {
            intvalue--;
        }
        return intvalue;
    }
    
    public static float ceil(final float value) {
        if (value != value) {
            // NaN
            return value;
        }
        if (value >= TWO_POW_23 || value <= -TWO_POW_23) {
            return value;
        }
        int intvalue = (int) value;
        if (value > 0 && intvalue != value) {
            intvalue++;
        }
        return intvalue;
    }
    
    public static float rint(final float value) {
        if (value != value) {
            // NaN
            return value;
        }
        if (value > 0 && value < TWO_POW_23) {
            return (TWO_POW_23 + value) - TWO_POW_23;
        } else if (value < 0 && value > -TWO_POW_23) {
            return (-TWO_POW_23 + value) + TWO_POW_23;
        }
        return value;
    }
    
    public static int round(final float value) {
        return (int) rint(value);
    }
    
    public static long roundl(final float value) {
        return (long) rint(value);
    }
    
    public static boolean equals(final Vector2fc v1, final Vector2fc v2) {
        return v1 == v2 || (v1 != null && v2 != null && v1.x() == v2.x() && v1.y() == v2.y());
    }
    
    public static boolean equals(final Vector3fc v1, final Vector3fc v2) {
        return v1 == v2 || (v1 != null && v2 != null && v1.x() == v2.x() && v1.y() == v2.y() && v1.z() == v2.z());
    }
    
    public static boolean equals(final Vector4fc v1, final Vector4fc v2) {
        return v1 == v2 || (v1 != null && v2 != null && v1.x() == v2.x() && v1.y() == v2.y() && v1.z() == v2.z()
                && v1.w() == v2.w());
    }
    
    public static boolean equals(final Quaternionfc q1, final Quaternionfc q2) {
        return q1 == q2 || (q1 != null && q2 != null && q1.x() == q2.x() && q1.y() == q2.y() && q1.z() == q2.z()
                && q1.w() == q2.w());
    }
}
