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

import java.util.Random;

import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import de.omnikryptec.core.display.Display;

public class Maths {

    public static final double STEFANBOLTZMANN = 5.670373 * java.lang.Math.pow(10, -8);
    private static final double SOMECONSTANT = 1 / java.lang.Math.pow(2 * Math.PI, 0.5);

    public static final Vector3f X = new Vector3f(1, 0, 0);
    public static final Vector3f Y = new Vector3f(0, 1, 0);
    public static final Vector3f Z = new Vector3f(0, 0, 1);
    public static final Vector3f ZERO = new Vector3f(0, 0, 0);
    public static final Vector3f ONE = new Vector3f(1, 1, 1);
    public static final double E = java.lang.Math.E;
    public static final double PI = java.lang.Math.PI;

    /**
     * 2^52 - double numbers this large must be integral (no fraction) or NaN or
     * Infinite
     */
    private static final double TWO_POWER_52 = 4503599627370496.0;

    /**
     * Get the largest whole number smaller than x.
     *
     * @param x number from which floor is requested
     * @return a double number f such that f is an integer f less or equal x
     * less f + 1.0
     */
    public static double fastFloord(double x) {
        if (x != x) { // NaN
            return x;
        }

        if (x >= TWO_POWER_52 || x <= -TWO_POWER_52) {
            return x;
        }

        long y = (long) x;
        if (x < 0 && y != x) {
            y--;
        }
        return y;
    }

    public static long fastFloor(double x) {
        return (long) fastFloord(x);
    }

    public static long fastFloor(float x) {
        return (long) fastFloord(x);
    }

    /**
     * Get the smallest whole number larger than x.
     *
     * @param x number from which ceil is requested
     * @return a double number c such that c is an integer c - 1.0 less x less
     * or equal c
     */
    public static double fastCeild(double x) {
        if (x != x) { // NaN
            return x;
        }

        double y = fastFloor(x);
        if (y == x) {
            return y;
        }

        y += 1.0;

        if (y == 0) {
            return x * y;
        }

        return y;
    }

    public static Matrix4f createTransformationMatrix(Vector3f position, Vector3f rotation, Vector3f scale) {
        return createTransformationMatrix(position, rotation, scale, null);
    }

    public static Matrix4f createTransformationMatrix(Vector3f position, Vector3f rotation, Vector3f scale,
            Matrix4f matrix) {
        if (matrix == null) {
            matrix = new Matrix4f();
        }
        matrix.identity();
        matrix.translate(position);
        matrix.rotate((float) Math.toRadians(rotation.x), X);
        matrix.rotate((float) Math.toRadians(rotation.y), Y);
        matrix.rotate((float) Math.toRadians(rotation.z), Z);
        matrix.scale(scale);
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(rotation.x), X);
        matrix.rotate((float) Math.toRadians(rotation.y), Y);
        matrix.rotate((float) Math.toRadians(rotation.z), Z);
        matrix.scale(scale);
        return matrix;
    }

    public static Matrix4f createEmptyTransformationMatrix() {
        return createEmptyTransformationMatrix(ZERO);
    }

    public static Matrix4f createEmptyTransformationMatrix(Vector3f pos) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(pos);
        return matrix;
    }

    public static Matrix4f newPerspectiveProjection(float fovdeg, float far, float near) {
        return newPerspectiveProjection(fovdeg, far, near, Display.getWidth(), Display.getHeight());
    }

    public static Matrix4f newPerspectiveProjection(float fovdeg, float far, float near, float width, float height) {
        return new Matrix4f().setPerspective((float) Math.toRadians(fovdeg), width / height, near, far);
    }

    public static Matrix4f newOrthographicProjection(float left, float right, float bottom, float top, float near,
            float far) {
        return new Matrix4f().setOrtho(left, right, bottom, top, near, far);
    }

    public static Matrix4f newOrthographicProjection2D(float x, float y, float width, float height) {
        return newOrthographicProjection(x, x + width, y + height, y, 1, -1);
    }

    public static Matrix4f newOrthographicProjection2D(float x, float y, float width, float height, float near,
            float far) {
        return newOrthographicProjection(x, x + width, y, y + height, near, far);
    }

    public static boolean fastEquals2f(Vector2f one, Vector2f sec) {
        return one != null && sec != null && one.x == sec.x && one.y == sec.y;
    }

    public static boolean fastEquals3f(Vector3f one, Vector3f sec) {
        return one != null && sec != null && one.x == sec.x && one.y == sec.y && one.z == sec.z;
    }

    public static boolean fastEquals4f(Vector4f one, Vector4f sec) {
        return one != null && sec != null && one.x == sec.x && one.y == sec.y && one.z == sec.z && one.w == sec.w;
    }

    public static boolean fastEquals4f(Quaternionf one, Quaternionf sec) {
        return one != null && sec != null && one.x == sec.x && one.y == sec.y && one.z == sec.z && one.w == sec.w;
    }

    public static final float[] intArrayToFloatArray(int[] input) {
        final float[] output = new float[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    public static final int[] floatArrayToIntArray(float[] input) {
        final int[] output = new int[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (int) (input[i] + 0.5);
        }
        return output;
    }

    public static final float clamp(float in, float min, float max) {
        return in < min ? min : (in > max ? max : in);
    }

    public static final int clamp(int input, int min, int max) {
        return Math.min(max, Math.max(min, input));
    }

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static Vector2f rotateAround(float x, float y, float radius, double radians) {
        Vector2f vec = new Vector2f((float) (radius * Math.sin(radians)), (float) (radius * Math.cos(radians)));
        vec.add(x, y);
        return vec;
    }

    private static Vector4f tmp4f = new Vector4f();
    private static Vector3f rotateAxis = new Vector3f();
    private static Matrix4f rotationMatrix = new Matrix4f();
    private static Matrix3f helpmatrix = new Matrix3f();

    /**
     *
     * @param random a random number generator
     * @param coneDirection direction of the cone
     * @param coneangle max coneangle in radians
     * @return a random normal vector within the cone
     */
    public static Vector3f generateRandomUnitVectorWithinCone(Random random, Vector3f coneDirection, double coneangle) {
        float cosAngle = (float) Math.cos(coneangle);
        double theta = getRandomRotationf(random);
        float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));

        tmp4f.set(x, y, z, 1);
        if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
            coneDirection.cross(Maths.Z, rotateAxis);
            rotateAxis.normalize();
            float rotateAngle = (float) Math.acos(coneDirection.dot(Maths.Z));
            rotationMatrix.identity();
            rotationMatrix.rotate(-rotateAngle, rotateAxis);
            rotationMatrix.transform(tmp4f);
        } else if (coneDirection.z == -1) {
            tmp4f.z *= -1;
        }
        return new Vector3f(tmp4f.x, tmp4f.y, tmp4f.z).normalize();
    }

    public static Vector3f generateRandomUnitVector(Random random) {
        double theta = getRandomRotationf(random);
        float z = (random.nextFloat() * 2) - 1;
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        return new Vector3f((float) (rootOneMinusZSquared * Math.cos(theta)),
                (float) (rootOneMinusZSquared * Math.sin(theta)), z).normalize();
    }

    public static float getErroredValue(Random random, float average, float errorMargin) {
        float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
        return average + offset;
    }

    public static Vector3f getRandomPointOnLine(Random random, Vector3f direction, Vector3f center, float linelength) {
        Vector3f vec = direction.normalize(new Vector3f()).mul(linelength / 2);
        vec.mul(random.nextFloat() * 2 - 1);
        return vec.add(center);
    }

    public static Vector3f getRandomPointOnLine(Random random, Vector3f p1, Vector3f p2) {
        Vector3f vec = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        vec.mul(random.nextFloat());
        return vec.add(p1);
    }

    public static Vector3f getRandomPointInCircle(Random random, Vector3f middle, float radius, Vector3f direction) {
        Vector3f directionn = direction.normalize(new Vector3f());
        double rotation = getRandomRotation(random);
        double calculatedradius = (radius * Math.sqrt(random.nextDouble()));
        double acosdot = Math.acos(directionn.dot(Y));
        Vector3f cross = Maths.Y.cross(directionn, new Vector3f()).normalize();
        if (acosdot == 0 || (Float.isNaN(cross.x) && Float.isNaN(cross.y) && Float.isNaN(cross.z))) {
            return new Vector3f((float) ((Math.cos(rotation) * calculatedradius)), 0,
                    (float) ((Math.sin(rotation) * calculatedradius))).add(middle);
        } else {
            return helpmatrix.identity().rotate(new AxisAngle4f((float) acosdot, cross.x, cross.y, cross.z))
                    .transform(new Vector3f((float) ((Math.cos(rotation) * calculatedradius)), 0,
                            (float) ((Math.sin(rotation) * calculatedradius))))
                    .add(middle);
        }
    }

    public static Vector3f getRandomPointInCircle(Random random, Vector3f middle, float radius) {
        double rotation = getRandomRotation(random);
        double calculatedradius = (radius * Math.sqrt(random.nextDouble()));
        return new Vector3f((float) (middle.x + (Math.cos(rotation) * calculatedradius)), middle.y,
                (float) (middle.z + (Math.sin(rotation) * calculatedradius)));
    }

    public static Vector3f getRandomPointInSphere(Random random, Vector3f middle, float radius) {
        float calculatedradius = (float) (radius * Math.sqrt(random.nextDouble()));
        return generateRandomUnitVector(random).mul(calculatedradius).add(middle);
    }

    public static double getRandomRotation(Random r) {
        return (r.nextDouble() * 2 * Math.PI);
    }

    public static float getRandomRotationf(Random r) {
        return (float) getRandomRotation(r);
    }

    public static Vector2f cartesianToIsometric(Vector2f vec, Vector2f target) {
        if (target == null) {
            target = new Vector2f();
        }
        return target.set(vec.x - vec.y, (vec.x + vec.y) / 2f);
    }

    public static Vector2f isometricToCartesian(Vector2f vec, Vector2f target) {
        if (target == null) {
            target = new Vector2f();
        }
        return target.set((2 * vec.y + vec.x) / 2, (2 * vec.y - vec.x) / 2);
    }

    public static <T> T getWeightedRandom(Random random, T[] ts, int[] weights) {
        int sum = 0;
        for (int i : weights) {
            sum += i;
        }
        int rand = random.nextInt(sum - 1) + 1;
        for (int i = 0; i < ts.length; i++) {
            rand -= weights[i];
            if (rand <= 0) {
                return ts[i];
            }
        }
        return ts[0];
    }
    
    /**
     *
     * @param x the value
     * @param min inclusive
     * @param max exclusive
     * @return
     */
    public static float mod(float x, float min, float max) {
        return min + ((x - min) % (max - min));
    }

    /**
     * normalizes x
     *
     * @param x value
     * @param min smallest value possible
     * @param range difference between min and biggest value possible
     * @return [0.0-1.0]
     */
    public static float normalize(float x, float min, float range) {
        return (x - min) / range;
    }

    public static float unnormalize(float n, float min, float range) {
        return n * range + min;
    }

    /**
     * inverts x
     *
     * @param x value
     * @param min smallest value possible
     * @param range difference between min and biggest value possible
     * @return
     */
    public static float invert(float x, float min, float range) {
        return range - x + 2 * min;
    }

    public static int toPowerOfTwo(int n) {
        return 1 << (32 - Integer.numberOfLeadingZeros(n - 1));
    }

    public static boolean isPowerOfTwo(int n) {
        return (n & -n) == n;
    }

}
