/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.util.data;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.joml.Vector4f;

import de.omnikryptec.util.data.fc.FloatCollector;
import de.omnikryptec.util.math.Interpolator;
import de.omnikryptec.util.math.Mathf;

/**
 * A utility class representing a color consisting of the red, green, blue and
 * alpha component.<br>
 * This class also provides various utility functions assoicated with color.<br>
 * This class uses normalized values in the range [0, 1]
 *
 * @author pcfreak9000
 *
 */
public class Color implements Cloneable {
    
    public static final Color ZERO = new Color(0, 0, 0, 0);
    public static final Color ONE = new Color(1, 1, 1, 1);
    
    private static final double TEMPERATURE_RED_EXP_CONST = 329.698727446;
    private static final double TEMPERATURE_RED_EXP = -0.1332047592;
    private static final double TEMPERATURE_GREEN_LN_CONST = 99.4708025861;
    private static final double TEMPERATURE_GREEN_LN_SUB = 161.1195681661;
    private static final double TEMPERATURE_GREEN_EXP_CONST = 288.1221695283;
    private static final double TEMPERATURE_GREEN_EXP = -0.0755148492;
    private static final double TEMPERATURE_BLUE_LN_CONST = 138.5177312231;
    private static final double TEMPERATURE_BLUE_LN_SUB = 305.0447927307;
    
    /**
     * Interpolates between to {@link Color}s. A ratio of 0 means that the output
     * color is color1, a ratio of 1 means that the output color is color2.<br>
     * This function clones color1.
     *
     * @param color1   the first color
     * @param color2   the second color
     * @param ratio    the interpolation ratio
     * @param interpol the interpolation function
     * @return the interpolated color
     *
     * @see Color#interpolate(Color, float, Interpolator)
     * @see Color#lerp(Color, Color, float)
     */
    public static Color interpolate(final Color color1, final Color color2, final float ratio,
            final Interpolator interpol) {
        return color1.clone().interpolate(color2, ratio, interpol);
    }
    
    /**
     * Linearly interpolates between to {@link Color}s. A ratio of 0 means that the
     * output color is color1, a ratio of 1 means that the output color is
     * color2.<br>
     * This function clones color1.
     *
     * @param color1 the first color
     * @param color2 the second color
     * @param ratio  the interpolation ratio
     * @return the interpolated color
     *
     * @see Color#lerp(Color, float)
     * @see Color#interpolate(Color, Color, float, Interpolator)
     */
    public static Color lerp(final Color color1, final Color color2, final float ratio) {
        return color1.clone().lerp(color2, ratio);
    }
    
    /**
     * Converts a color temperature into a {@link Color}. The color temperature
     * should be in the range [0, 50000].
     *
     * @param colTemperature the color temperature in kelvin 50000]
     * @return the converted Color
     */
    public static Color ofTemperature(float colTemperature) {
        float red = 0;
        float green = 0;
        float blue = 0;
        colTemperature /= 100.0f;
        if (colTemperature <= 66.0f) {
            red = 255;
        } else {
            red = colTemperature - 60.0f;
            red = (float) (TEMPERATURE_RED_EXP_CONST * Math.pow(red, TEMPERATURE_RED_EXP));
        }
        if (colTemperature <= 66.0f) {
            green = colTemperature;
            green = (float) (TEMPERATURE_GREEN_LN_CONST * Math.log(green) - TEMPERATURE_GREEN_LN_SUB);
        } else {
            green = colTemperature - 60.0f;
            green = (float) (TEMPERATURE_GREEN_EXP_CONST * Math.pow(green, TEMPERATURE_GREEN_EXP));
        }
        if (colTemperature >= 66.0f) {
            blue = 255.0f;
        } else if (colTemperature <= 19.0f) {
            blue = 0.0f;
        } else {
            blue = colTemperature - 10.0f;
            blue = (float) (TEMPERATURE_BLUE_LN_CONST * Math.log(blue) - TEMPERATURE_BLUE_LN_SUB);
        }
        red = Mathf.clamp(red, 0.0f, 255.0f);
        green = Mathf.clamp(green, 0.0f, 255.0f);
        blue = Mathf.clamp(blue, 0.0f, 255.0f);
        return new Color(red / 255.0f, green / 255.0f, blue / 255.0f);
    }
    
    //Maybe make array for the FloatCollecor? But shouldn't be that impactfull
    private final float[] d = new float[4]; //getR, getG, getB, getA;
    
    /**
     * Creates a new {@link Color} and initializes it as r=1 g=1 b=1 a=1.
     */
    public Color() {
        this(1, 1, 1, 1);
    }
    
    /**
     * Creates a new {@link Color} and initializes it as given but a=1.
     */
    public Color(final float r, final float g, final float b) {
        this(r, g, b, 1);
    }
    
    /**
     * Creates a new {@link Color} and initializes it as given.
     */
    public Color(final float r, final float g, final float b, final float a) {
        set(r, g, b, a);
    }
    
    public Color(final int rgb) {
        this(rgb, false);
    }
    
    public Color(final int rgba, final boolean hasAlpha) {
        setRGBA(rgba, hasAlpha);
    }
    
    public Color(final float[] array) {
        set(array);
    }
    
    public Color(final Vector4f color) {
        setFrom(color);
    }
    
    /**
     * Creates a new {@link Color} and initializes it with the given awt color.
     *
     * @param color the {@link java.awt.Color}
     */
    public Color(final java.awt.Color color) {
        this(color.getRGBComponents(null));
    }
    
    /**
     * Creates a new {@link Vector4f} and fills it components as x=r, y=g, z=b and
     * w=a.
     */
    public final Vector4f getVector4f() {
        return new Vector4f(this.getR(), this.getG(), this.getB(), this.getA());
    }
    
    /**
     * Creates a new float[] and fills it with the r, g, b, and a values of this
     * {@link Color}.
     */
    public final float[] getArray() {
        return this.d.clone();
    }
    
    public final float getR() {
        return this.d[0];
    }
    
    public final Color setR(final float r) {
        this.d[0] = r;
        return this;
    }
    
    public final float getG() {
        return this.d[1];
    }
    
    public final Color setG(final float g) {
        this.d[1] = g;
        return this;
    }
    
    public final float getB() {
        return this.d[2];
    }
    
    public final Color setB(final float b) {
        this.d[2] = b;
        return this;
    }
    
    public final float getA() {
        return this.d[3];
    }
    
    public final Color setA(final float a) {
        this.d[3] = a;
        return this;
    }
    
    public final float get(int index) {
        return this.d[index];
    }
    
    public final Color set(int index, float value) {
        this.d[index] = value;
        return this;
    }
    
    public final int getRGB() {
        return getRGBA(false);
    }
    
    public final Color setRGB(final int rgb) {
        return setRGBA(rgb, false);
    }
    
    public final int getRGBA(final boolean withAlpha) {
        final int r = (int) (getR() * 255) << 16;
        final int g = (int) (getG() * 255) << 8;
        final int b = (int) (getB() * 255);
        int a = (255 << 24);
        if (withAlpha) {
            a = (int) (getA() * 255) << 24;
        }
        return (r + g + b + a);
    }
    
    public final java.awt.Color getAWTColor() {
        return new java.awt.Color(getR(), getG(), getB(), getA());
    }
    
    public final Color set(final float r, final float g, final float b) {
        set(r, g, b, 1);
        return this;
    }
    
    public final Color set(final float r, final float g, final float b, final float a) {
        this.d[0] = r;
        this.d[1] = g;
        this.d[2] = b;
        this.d[3] = a;
        return this;
    }
    
    public final Color setRGBA(final int rgba, final boolean hasAlpha) {
        if (hasAlpha) {
            setA(((rgba >> 24) & 0xFF) / 255.0F);
        } else {
            setA(1.0F);
        }
        setR(((rgba >> 16) & 0xFF) / 255.0F);
        setG(((rgba >> 8) & 0xFF) / 255.0F);
        setB(((rgba) & 0xFF) / 255.0F);
        return this;
    }
    
    public final Color setFrom(final Vector4f v) {
        return set(v.x, v.y, v.z, v.w);
    }
    
    public final Color set(final Color c) {
        set(c.d);
        return this;
    }
    
    public final Color set(final float[] array) {
        setR(array[0]);
        setG(array[1]);
        setB(array[2]);
        setA(array.length > 3 ? array[3] : 1);
        return this;
    }
    
    public final Color setFromAWT(final java.awt.Color color) {
        set(color.getRGBComponents(null));
        return this;
    }
    
    public Color setAllRGB(float i) {
        set(i, i, i, 1);
        return this;
    }
    
    public Color setAll(final float i) {
        set(i, i, i, i);
        return this;
    }
    
    public Color interpolate(final Color color, final float ratio, final Interpolator interpol) {
        return lerp(color, interpol.interpolate(ratio));
    }
    
    public Color lerp(final Color color, final float ratio) {
        final float inv = 1.0f - ratio;
        for (int i = 0; i < 4; i++) {
            this.d[i] = this.d[i] * inv + color.d[i] * ratio;
        }
        return this;
    }
    
    public Color clip() {
        for (int i = 0; i < 4; i++) {
            this.d[i] = Mathf.min(1.0f, Mathf.max(0.0f, this.d[i]));
        }
        return this;
    }
    
    public Color mulRGB(float f) {
        for (int i = 0; i < 3; i++) {
            this.d[i] *= f;
        }
        return this;
    }
    
    public Color mul(float f) {
        for (int i = 0; i < 4; i++) {
            this.d[i] *= f;
        }
        return this;
    }
    
    public Color mulRGB(Color c) {
        for (int i = 0; i < 3; i++) {
            this.d[i] *= c.d[i];
        }
        return this;
    }
    
    public Color mul(Color c) {
        for (int i = 0; i < 4; i++) {
            this.d[i] *= c.d[i];
        }
        return this;
    }
    
    public Color add(Color c) {
        for (int i = 0; i < 4; i++) {
            this.d[i] += c.d[i];
        }
        return this;
    }
    
    private float maxHelper(int l) {
        float max = this.d[0];
        for (int i = 1; i < l; i++) {
            max = Mathf.max(max, this.d[i]);
        }
        return max;
    }
    
    public float max() {
        return maxHelper(4);
    }
    
    public float maxRGB() {
        return maxHelper(3);
    }
    
    private float minHelper(int l) {
        float min = this.d[0];
        for (int i = 1; i < l; i++) {
            min = Mathf.min(min, this.d[i]);
        }
        return min;
    }
    
    public float min() {
        return minHelper(4);
    }
    
    public float minRGB() {
        return minHelper(3);
    }
    
    private float sHelp(int l) {
        float r = 0;
        for (int i = 0; i < l; i++) {
            r += Mathf.square(this.d[i]);
        }
        return r;
    }
    
    public float lengthSquare() {
        return sHelp(4);
    }
    
    public float lengthSquareRGB() {
        return sHelp(3);
    }
    
    public float length() {
        return Mathf.sqrt(lengthSquare());
    }
    
    public float lengthRGB() {
        return Mathf.sqrt(lengthSquareRGB());
    }
    
    public Color randomizeRGBA() {
        return randomizeRGB((float) Math.random());
    }
    
    public Color randomizeRGB(final float a) {
        return set((float) Math.random(), (float) Math.random(), (float) Math.random(), a);
    }
    
    public Color randomizeRGB() {
        return randomizeRGB(1);
    }
    
    @Override
    public final Color clone() {
        try {
            return ((Color) super.clone());
        } catch (final CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String toString() {
        return "Color: [R=" + getR() + " G=" + getG() + " B=" + getB() + " A=" + getA() + "]";
    }
    
    public boolean equalsRGB(float r, float g, float b) {
        return d[0] == r && d[1] == g && d[2] == b;
    }
    
    public boolean equals(float r, float g, float b, float a) {
        return d[0] == r && d[1] == g && d[2] == b && d[3] == a;
    }
    
    public boolean equalsRGB(Color c) {
        return c != null && (c == this || this.equalsRGB(c.getR(), c.getG(), c.getB()));
    }
    
    public void get(FloatBuffer floatBuffer) {
        floatBuffer.put(d);
    }
    
    public void get(FloatCollector flc) {
        flc.put(d);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(d);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Color)) {
            return false;
        }
        Color other = (Color) obj;
        if (!Arrays.equals(d, other.d)) {
            return false;
        }
        return true;
    }
    
}
