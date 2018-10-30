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

package de.omnikryptec.old.gameobject;

import de.omnikryptec.old.graphics.SpriteBatch;
import de.omnikryptec.old.resource.texture.Texture;
import de.omnikryptec.old.util.EnumCollection.FixedSizeMode;
import de.omnikryptec.util.data.Color;

public class Sprite extends GameObject2D {

    private Texture texture;
    private Color color = new Color(1, 1, 1, 1);
    private float layer = 0;
    private float w, h;
    private FixedSizeMode fmode = FixedSizeMode.OFF;

    public Sprite() {
	this("", null, null);
    }

    public Sprite(Texture t) {
	this("", t);
    }

    public Sprite(String name, Texture t) {
	this(name, t, null);
    }

    public Sprite(Texture t, GameObject2D p) {
	this("", t, p);
    }

    public Sprite(String name, GameObject2D p) {
	this(name, null, p);
    }

    public Sprite(String name, Texture texture, GameObject2D parent) {
	super(name, parent);
	this.texture = texture;
    }

    public Texture getTexture() {
	return texture;
    }

    public Color getColor() {
	return color;
    }

    public float getLayer() {
	return layer;
    }

    public Sprite setTexture(Texture t) {
	this.texture = t;
	return this;
    }

    public Sprite setColor(Color c) {
	this.color = c;
	return this;
    }

    public Sprite setLayer(float l) {
	this.layer = l;
	return this;
    }

    public void paint(SpriteBatch batch) {
	batch.draw(this);
    }

    public Sprite setFixedSize(float w, float h) {
	this.w = w;
	this.h = h;
	return this;
    }

    public Sprite setFixedWidthAR(float w) {
	float ar = texture.getHeight() / texture.getWidth();
	this.w = w;
	this.h = ar * w;
	return this;
    }

    public Sprite setFixedHeightAR(float h) {
	float ar = texture.getWidth() / texture.getHeight();
	this.w = ar * h;
	this.h = h;
	return this;
    }

    public Sprite setFixedSizeMode(FixedSizeMode fm) {
	this.fmode = fm;
	return this;
    }

    public float getWidth() {
	switch (fmode) {
	case ALLOW_SCALING:
	    return w * getTransform().getScale().x;
	case OFF:
	    return texture.getWidth() * getTransform().getScale().x;
	case ON:
	    return w;
	default:
	    return -1;
	}
    }

    public float getHeight() {
	switch (fmode) {
	case ALLOW_SCALING:
	    return h * getTransform().getScale().y;
	case OFF:
	    return texture.getHeight() * getTransform().getScale().y;
	case ON:
	    return h;
	default:
	    return -1;
	}
    }

}
