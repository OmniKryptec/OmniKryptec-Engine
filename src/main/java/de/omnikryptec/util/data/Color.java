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

package de.omnikryptec.util.data;

import org.joml.Vector4f;

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
    private float r, g, b, a;
    
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
        return new Vector4f(this.r, this.g, this.b, this.a);
    }
    
    /**
     * Creates a new float[] and fills it with the r, g, b, and a values of this
     * {@link Color}.
     */
    public final float[] getArray() {
        return new float[] { this.r, this.g, this.b, this.a };
    }
    
    public final float getR() {
        return this.r;
    }
    
    public final Color setR(final float r) {
        this.r = r;
        return this;
    }
    
    public final float getG() {
        return this.g;
    }
    
    public final Color setG(final float g) {
        this.g = g;
        return this;
    }
    
    public final float getB() {
        return this.b;
    }
    
    public final Color setB(final float b) {
        this.b = b;
        return this;
    }
    
    public final float getA() {
        return this.a;
    }
    
    public final Color setA(final float a) {
        this.a = a;
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
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
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
        set(c.r, c.g, c.b, c.a);
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
    
    public Color setAll(final float i) {
        set(i, i, i, i);
        return this;
    }
    
    public Color interpolate(final Color color, final float ratio, final Interpolator interpol) {
        return lerp(color, interpol.interpolate(ratio));
    }
    
    public Color lerp(final Color color, final float ratio) {
        final float inv = 1.0f - ratio;
        this.r = this.r * inv + color.r * ratio;
        this.g = this.g * inv + color.g * ratio;
        this.b = this.b * inv + color.b * ratio;
        this.a = this.a * inv + color.a * ratio;
        return this;
    }
    
    public Color clip() {
        this.r = Math.min(1.0f, Math.max(0.0f, this.r));
        this.g = Math.min(1.0f, Math.max(0.0f, this.g));
        this.b = Math.min(1.0f, Math.max(0.0f, this.b));
        this.a = Math.min(1.0f, Math.max(0.0f, this.a));
        return this;
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
    
}
