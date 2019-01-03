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

package de.omnikryptec.old.resource.texture;

import de.omnikryptec.old.graphics.OpenGL;
import de.omnikryptec.old.resource.loader.ResourceObject;
import org.lwjgl.opengl.GL11;

public abstract class Texture implements ResourceObject {

    public static final int MAX_SUPPORTED_TEXTURE_UNITS = 10;

    private static Texture[] lastBoundTexture = new Texture[MAX_SUPPORTED_TEXTURE_UNITS];

    private final String name;
    private boolean alwaysBind = false;
    private float[] uvs = { 0, 0, 1, 1 };

    public Texture(String name) {
	this(name, false);
    }

    public Texture(String name, float u, float v, float u2, float v2) {
	this(name, false, u, v, u2, v2);
    }

    public Texture(String name, boolean alwaysBind) {
	this(name, alwaysBind, 0, 0, 1, 1);
    }

    public Texture(String name, boolean alwaysBind, float u, float v, float u2, float v2) {
	this.name = name;
	this.alwaysBind = alwaysBind;
	uvs[0] = u;
	uvs[1] = v;
	uvs[2] = u2;
	uvs[3] = v2;
    }

    protected Texture setUVs(float u, float v, float u2, float v2) {
	uvs[0] = u;
	uvs[1] = v;
	uvs[2] = u2;
	uvs[3] = v2;
	return this;
    }

    public Texture invertV() {
	float tmp = uvs[3];
	uvs[3] = uvs[1];
	uvs[1] = tmp;
	return this;
    }

    public Texture invertU() {
	float tmp = uvs[2];
	uvs[2] = uvs[0];
	uvs[0] = tmp;
	return this;
    }

    /**
     * 
     * @param unit
     * @param info
     * @return true if this texture has been bound.
     */
    public final boolean bindToUnitOptimized(int unit, int... info) {
	if (this != lastBoundTexture[unit] || alwaysBind) {
	    bindToUnit(unit, info);
	    lastBoundTexture[unit] = this;
	    return true;
	}
	return false;
    }

    public float[] getUVs() {
	return uvs;
    }

    public boolean bindAlways() {
	return alwaysBind;
    }

    @Override
    public final String getName() {
	return name;
    }

    @Override
    public String toString() {
	return getClass() + ": " + getName();
    }

    public static void resetLastBoundTexture() {
	for (int i = 0; i < lastBoundTexture.length; i++) {
	    lastBoundTexture[i] = null;
	}
    }

    public static void unbindActive() {
	bindTexture(GL11.GL_TEXTURE_2D, 0);
	resetLastBoundTexture();
    }

    public static void unbindAllActive() {
	for (int i = 0; i < lastBoundTexture.length; i++) {
	    OpenGL.gl13activeTextureZB(i);
	    bindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	resetLastBoundTexture();
    }

    public static void bindAndReset(int type, int id) {
	bindTexture(type, id);
	resetLastBoundTexture();
    }

    /**
     * should only be used in {@link #bindToUnit(int, int...)}
     * 
     * @param type
     * @param id
     */
    protected static void bindTexture(int type, int id) {
	OpenGL.gl11bindTexture(type, id);
    }

    protected abstract void bindToUnit(int unit, int... info);

    public abstract float getWidth();

    public abstract float getHeight();
}
