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

package de.omnikryptec.util.data;

import org.joml.Vector4f;

public class Color implements Cloneable{

    private Vector4f color = new Vector4f();

    public Color() {
        this(1, 1, 1, 1);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public Color(float r, float g, float b, float a) {
        color.set(r, g, b, a);
    }

    public Color(int rgb) {
        this(rgb, false);
    }

    public Color(int rgba, boolean hasAlpha) {
        setRGBA(rgba, hasAlpha);
    }

    public Color(float[] array) {
        setFrom(array);
    }

    public Color(Vector4f color) {
        this.color = color;
    }

    /**
     * Cloning constructor
     * @param c to be cloned
     */
    private Color(Color c) {
    	this.color.set(c.color);
    }

    public Color(java.awt.Color color) {
        this(color.getRGBComponents(null));
    }

    public final Vector4f getVector4fNew() {
        return new Vector4f(color);
    }

    public final Vector4f getVector4f() {
        return color;
    }

    public final float[] getArray() {
        return new float[]{color.x(), color.y(), color.z(), color.w()};
    }

    public final float getR() {
        return color.x;
    }

    public final float getG() {
        return color.y;
    }

    public final float getB() {
        return color.z;
    }

    public final float getA() {
        return color.w;
    }

    public final Color setFrom(Vector4f v) {
        color.set(v);
        return this;
    }

    public final Color set(Vector4f v) {
        this.color = v;
        return this;
    }

    public final Color setFrom(float[] array) {
        setR(array[0]);
        setG(array[1]);
        setB(array[2]);
        setA(array.length > 3 ? array[3] : 1);
        return this;
    }

    public final Color setR(float r) {
        color.x = r;
        return this;
    }

    public final Color setG(float g) {
        color.y = g;
        return this;
    }

    public final Color setB(float b) {
        color.z = b;
        return this;
    }

    public final Color setA(float a) {
        color.w = a;
        return this;
    }

    public final Color setFrom(Color c) {
        setFrom(c.color);
        return this;
    }

    public final Color setFromAWT(java.awt.Color color) {
        setFrom(color.getRGBComponents(null));
        return this;
    }

    public final java.awt.Color getAWTColor() {
        return new java.awt.Color(getR(), getG(), getB(), getA());
    }

    @Override
    public final Color clone() {
        return new Color(this);
    }

    public final Color set(float r, float g, float b) {
        set(r, g, b, 1);
        return this;
    }

    public final Color set(float r, float g, float b, float a) {
        setR(r);
        setG(g);
        setB(b);
        setA(a);
        return this;
    }

    public final Color setRGB(int rgb) {
        return setRGBA(rgb, false);
    }

    public final Color setRGBA(int rgba, boolean hasAlpha) {
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

    public final int getRGB() {
        return getRGBA(false);
    }

    public final int getRGBA(boolean withAlpha) {
        final int r = (int) (getR() * 255) << 16;
        final int g = (int) (getG() * 255) << 8;
        final int b = (int) (getB() * 255);
        int a = (255 << 24);
        if (withAlpha) {
            a = (int) (getA() * 255) << 24;
        }
        return (r + g + b + a);
    }
    
	public Color setAll(float i) {
		color.set(i);
		return this;
	}
    
    public final Color blendWith(Color color, float ratio) {
        return blend(this, color, ratio);
    }

    public static final Color blend(Color color_1, Color color_2, float ratio) {
        if (ratio > 1.0f) {
            ratio = 1.0f;
        } else if (ratio < 0f) {
            ratio = 0.0f;
        }
        final float iRatio = 1.0f - ratio;
        final int i1 = color_1.getRGBA(true);
        final int i2 = color_2.getRGBA(true);
        final int a1 = (i1 >> 24 & 0xff);
        final int r1 = ((i1 & 0xff0000) >> 16);
        final int g1 = ((i1 & 0xff00) >> 8);
        final int b1 = (i1 & 0xff);
        final int a2 = (i2 >> 24 & 0xff);
        final int r2 = ((i2 & 0xff0000) >> 16);
        final int g2 = ((i2 & 0xff00) >> 8);
        final int b2 = (i2 & 0xff);
        final int a = (int) ((a1 * iRatio) + (a2 * ratio));
        final int r = (int) ((r1 * iRatio) + (r2 * ratio));
        final int g = (int) ((g1 * iRatio) + (g2 * ratio));
        final int b = (int) ((b1 * iRatio) + (b2 * ratio));
        return new Color(a << 24 | r << 16 | g << 8 | b);
    }
    
	@Override
	public String toString(){
		return "Color: [R="+getR()+" G="+getG()+" B="+getB()+" A="+getA()+"]";
	}

	public static Color randomRGBA() {
		return randomRGB((float)Math.random());
	}
	
	public static Color randomRGB(float a) {
		return new Color((float)Math.random(), (float)Math.random(),(float) Math.random(), a);
	}
	
	public static Color randomRGB() {
		return randomRGB(1);
	}



}
