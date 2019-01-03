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
import org.joml.Quaterniondc;
import org.joml.Vector2dc;
import org.joml.Vector3dc;
import org.joml.Vector4dc;

public strictfp class Mathd {

    public static final double PI = java.lang.Math.PI;
    public static final double E = java.lang.Math.E;

    /**
     * All double values above or equal to this value are integer numbers, all
     * double values below or equal to (-1) * this value are integer numbers.
     */
    private static final double TWO_POW_52 = 4503599627370496.0d;

    public static double clamp(final double in, final double min, final double max) {
        return in < min ? min : (in > max ? max : in);
    }

    public static double clamp01(final double in) {
        return in < 0.0 ? 0.0 : (in > 1.0 ? 1.0 : in);
    }

    public static double sin(final double rad) {
        return Math.sin(rad);
    }

    public static double cos(final double rad) {
        return Math.cos(rad);
    }

    public static double tan(final double rad) {
        return Math.tan(rad);
    }

    public static double arcsin(final double x) {
        return Math.asin(x);
    }

    public static double arccos(final double x) {
        return Math.acos(x);
    }

    public static double arctan(final double x) {
        return java.lang.Math.atan(x);
    }

    public static double arctan2(final double y, final double x) {
        return Math.atan2(y, x);
    }

    public static double sqrt(final double value) {
        return Math.sqrt(value);
    }

    public static double abs(final double value) {
        return value < 0.0 ? 0.0 - value : value;
    }

    public static double min(final double v0, final double v1) {
        return v0 < v1 ? v0 : v1;
    }

    public static double max(final double v0, final double v1) {
        return v0 > v1 ? v0 : v1;
    }

    public static double floor(final double value) {
        if (value != value) {
            // NaN
            return value;
        }
        if (value >= TWO_POW_52 || value <= -TWO_POW_52) {
            return value;
        }
        long intvalue = (long) value;
        if (value < 0 && intvalue != value) {
            intvalue--;
        }
        return intvalue;
    }

    public static double ceil(final double value) {
        if (value != value) {
            // NaN
            return value;
        }
        if (value >= TWO_POW_52 || value <= -TWO_POW_52) {
            return value;
        }
        long intvalue = (long) value;
        if (value > 0 && intvalue != value) {
            intvalue++;
        }
        return intvalue;
    }

    public static double rint(final double value) {
        if (value != value) {
            // NaN
            return value;
        }
        if (value > 0 && value < TWO_POW_52) {
            return (TWO_POW_52 + value) - TWO_POW_52;
        } else if (value < 0 && value > -TWO_POW_52) {
            return (-TWO_POW_52 + value) + TWO_POW_52;
        }
        return value;
    }

    public static long round(final double value) {
        return (long) rint(value);
    }

    public static int roundi(final double value) {
        return (int) rint(value);
    }

    public static boolean equals(final Vector2dc v1, final Vector2dc v2) {
        return v1 == v2 || (v1.x() == v2.x() && v1.y() == v2.y());
    }

    public static boolean equals(final Vector3dc v1, final Vector3dc v2) {
        return v1 == v2 || (v1.x() == v2.x() && v1.y() == v2.y() && v1.z() == v2.z());
    }

    public static boolean equals(final Vector4dc v1, final Vector4dc v2) {
        return v1 == v2 || (v1.x() == v2.x() && v1.y() == v2.y() && v1.z() == v2.z() && v1.w() == v2.w());
    }

    public static boolean equals(final Quaterniondc q1, final Quaterniondc q2) {
        return q1 == q2 || (q1.x() == q2.x() && q1.y() == q2.y() && q1.z() == q2.z() && q1.w() == q2.w());
    }
}
