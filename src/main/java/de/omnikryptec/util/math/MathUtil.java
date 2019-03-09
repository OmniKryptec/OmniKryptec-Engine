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

import java.util.Random;

import org.joml.Matrix4fc;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector4dc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class MathUtil {
    
    /**
     * Creates a viewport with a particular aspect ratio and the given width and
     * height. The viewport will be centered and of maximum size for the given
     * parameters. That means that either the final width or the final height might
     * be smaller than specified to maintain the given aspect ratio. <br>
     * Aspect ratios equal to or smaller than 0.0 are ignored and the viewport
     * becomes <code>{0,0,width,height}</code>
     *
     * @param aspectRatio the aspect ratio
     * @param w           the maximum width
     * @param h           the maximum height
     *
     * @return an array consisting of x, y, width, height; in that particular order
     */
    public static int[] calculateViewport(final double aspectRatio, final int w, final int h) {
        final int[] viewport = new int[4];
        viewport[0] = 0;
        viewport[1] = 0;
        viewport[2] = w;
        viewport[3] = h;
        if (aspectRatio > 0) {
            if ((double) w / (double) h <= aspectRatio) {
                viewport[3] = (int) (w * (1.0 / aspectRatio));
                viewport[1] = (int) ((h - viewport[3]) * 0.5);
            } else {
                viewport[2] = (int) (h * aspectRatio);
                viewport[0] = (int) ((w - viewport[2]) * 0.5);
            }
        }
        return viewport;
    }
    
    public static <T> T getWeightedRandom(final Random random, final T[] ts, final int[] weights) {
        int sum = 0;
        for (final int i : weights) {
            sum += i;
        }
        int rand = random.nextInt(sum) + 1;
        for (int i = 0; i < ts.length; i++) {
            if (rand <= weights[i]) {
                return ts[i];
            }
            rand -= weights[i];
        }
        return ts[0];
    }
    
    public static int getWeightedRandom(final Random random, final int[] ts, final int[] weights) {
        int sum = 0;
        for (final int i : weights) {
            sum += i;
        }
        int rand = random.nextInt(sum) + 1;
        for (int i = 0; i < ts.length; i++) {
            if (rand <= weights[i]) {
                return ts[i];
            }
            rand -= weights[i];
        }
        return ts[0];
    }
    
    public static Vector2f randomDirection2D(Random random, float begin, float end, Vector2f target) {
        if (target == null) {
            target = new Vector2f();
        }
        float rand = begin + (end - begin) * random.nextFloat();
        target.set(Mathf.cos(rand), Mathf.sin(rand));
        return target;
    }
    
    public static int toPowerOfTwo(final int n) {
        return 1 << (32 - Integer.numberOfLeadingZeros(n - 1));
    }
    
    public static boolean isPowerOfTwo(final int n) {
        return (n & -n) == n;
    }
    
    public static Vector2f relativeMousePosition(Vector2dc displayMousePosition, int[] viewport, Vector2f target) {
        if (target == null) {
            target = new Vector2f();
        }
        double x, y;
        if (displayMousePosition.x() < viewport[0] || displayMousePosition.x() > viewport[2] + viewport[0]) {
            x = -1;
        } else {
            x = displayMousePosition.x();
            x -= viewport[0];
            x /= viewport[2];
            x = 2 * x - 1;
        }
        if (displayMousePosition.y() < viewport[1] || displayMousePosition.y() > viewport[3] + viewport[1]) {
            y = -1;
        } else {
            y = displayMousePosition.y();
            y -= viewport[1];
            y /= viewport[3];
            y = 1.0 - y;
            y = 2 * y - 1;
        }
        target.set((float) x, (float) y);
        return target;
    }
    
    public static Vector2d relativeMousePosition(Vector2dc displayMousePosition, int[] viewport, Vector2d target) {
        if (target == null) {
            target = new Vector2d();
        }
        double x, y;
        if (displayMousePosition.x() < viewport[0] || displayMousePosition.x() > viewport[2] + viewport[0]) {
            x = -1;
        } else {
            x = displayMousePosition.x();
            x -= viewport[0];
            x /= viewport[2];
            x = 2 * x - 1;
        }
        if (displayMousePosition.y() < viewport[1] || displayMousePosition.y() > viewport[3] + viewport[1]) {
            y = -1;
        } else {
            y = displayMousePosition.y();
            y -= viewport[1];
            y /= viewport[3];
            y = 1.0 - y;
            y = 2 * y - 1;
        }
        target.set(x, y);
        return target;
    }
    
    public static Vector2f screenToWorldspace2D(Vector2fc relativeScreenPosition, Matrix4fc inverseViewProjection,
            Vector2f target) {
        if (target == null) {
            target = new Vector2f();
        }
        Vector4f vec4 = screenToWorldspace(relativeScreenPosition, inverseViewProjection, 0, null);
        target.set(vec4.x, vec4.y);
        return target;
    }
    
    public static Vector4f screenToWorldspace(Vector2fc relativeScreenPosition, Matrix4fc inverseViewprojection,
            float depth, Vector4f target) {
        if (target == null) {
            target = new Vector4f();
        }
        target.set(relativeScreenPosition, depth, 1);
        inverseViewprojection.transform(target);
        target.mul(1 / target.w, target);
        return target;
    }
    
    public static boolean isMouseInViewport(Vector2dc displayMousePosition, int[] viewport) {
        boolean out = (displayMousePosition.x() < viewport[0] || displayMousePosition.x() > viewport[2] + viewport[0])
                || (displayMousePosition.y() < viewport[1] || displayMousePosition.y() > viewport[3] + viewport[1]);
        return !out;
    }
    
    //Float vecs
    
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
    
    public static boolean equals(final Struct3f s1, final Struct3f s2) {
        return s1 == s2 || (s1.x == s2.x && s1.y == s2.y && s1.z == s2.z);
    }
    
    public static boolean equals(final Struct2f s1, final Struct2f s2) {
        return s1 == s2 || (s1.x == s2.x && s1.y == s2.y);
    }
    
    //double vecs
    
    public static boolean equals(final Vector2dc v1, final Vector2dc v2) {
        return v1 == v2 || (v1 != null && v2 != null && v1.x() == v2.x() && v1.y() == v2.y());
    }
    
    public static boolean equals(final Vector3dc v1, final Vector3dc v2) {
        return v1 == v2 || (v1 != null && v2 != null && v1.x() == v2.x() && v1.y() == v2.y() && v1.z() == v2.z());
    }
    
    public static boolean equals(final Vector4dc v1, final Vector4dc v2) {
        return v1 == v2 || (v1 != null && v2 != null && v1.x() == v2.x() && v1.y() == v2.y() && v1.z() == v2.z()
                && v1.w() == v2.w());
    }
    
    public static boolean equals(final Quaterniondc q1, final Quaterniondc q2) {
        return q1 == q2 || (q1 != null && q2 != null && q1.x() == q2.x() && q1.y() == q2.y() && q1.z() == q2.z()
                && q1.w() == q2.w());
    }
    
}
