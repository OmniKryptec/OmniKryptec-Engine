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

public class Color implements Cloneable {

	public static final Color blend(Color color1, Color color2, float ratio) {
		return color1.clone().blendWith(color2, ratio);
	}

	public static Color randomRGBA() {
		return randomRGB((float) Math.random());
	}

	public static Color randomRGB(float a) {
		return new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), a);
	}

	public static Color randomRGB() {
		return randomRGB(1);
	}

	private float r, g, b, a;

	public Color() {
		this(1, 1, 1, 1);
	}

	public Color(float r, float g, float b) {
		this(r, g, b, 1);
	}

	public Color(float r, float g, float b, float a) {
		set(r, g, b, a);
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
		setFrom(color);
	}

	public Color(java.awt.Color color) {
		this(color.getRGBComponents(null));
	}

	public final Vector4f getVector4f() {
		return new Vector4f(r, g, b, a);
	}

	public final float[] getArray() {
		return new float[] { r, g, b, a };
	}

	public final float getR() {
		return r;
	}

	public final float getG() {
		return g;
	}

	public final float getB() {
		return b;
	}

	public final float getA() {
		return a;
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

	public final java.awt.Color getAWTColor() {
		return new java.awt.Color(getR(), getG(), getB(), getA());
	}

	public final Color setR(float r) {
		this.r = r;
		return this;
	}

	public final Color setG(float g) {
		this.g = g;
		return this;
	}

	public final Color setB(float b) {
		this.b = b;
		return this;
	}

	public final Color setA(float a) {
		this.a = a;
		return this;
	}

	public final Color set(float r, float g, float b) {
		set(r, g, b, 1);
		return this;
	}

	public final Color set(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
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

	public final Color setFrom(Vector4f v) {
		return set(v.x, v.y, v.z, v.w);
	}

	public final Color setFrom(Color c) {
		set(c.r, c.g, c.b, c.a);
		return this;
	}

	public final Color setFrom(float[] array) {
		setR(array[0]);
		setG(array[1]);
		setB(array[2]);
		setA(array.length > 3 ? array[3] : 1);
		return this;
	}

	public final Color setFromAWT(java.awt.Color color) {
		setFrom(color.getRGBComponents(null));
		return this;
	}

	public Color setAll(float i) {
		set(i, i, i, i);
		return this;
	}

	public Color blendWith(Color color, float ratio) {
		float inv = 1.0f - ratio;
		r = r * inv + color.r * ratio;
		g = g * inv + color.g * ratio;
		b = b * inv + color.b * ratio;
		a = a * inv + color.a * ratio;
		return this;
	}

	public Color clip() {
		r = Math.min(1.0f, Math.max(0.0f, r));
		g = Math.min(1.0f, Math.max(0.0f, g));
		b = Math.min(1.0f, Math.max(0.0f, b));
		a = Math.min(1.0f, Math.max(0.0f, a));
		return this;
	}
	
	@Override
	public final Color clone() {
		try {
			return ((Color)super.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "Color: [R=" + getR() + " G=" + getG() + " B=" + getB() + " A=" + getA() + "]";
	}

}
