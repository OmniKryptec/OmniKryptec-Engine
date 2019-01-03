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

package de.omnikryptec.old.gui;

import de.omnikryptec.old.graphics.SpriteBatch;
import de.omnikryptec.old.resource.texture.Texture;
import de.omnikryptec.util.data.Color;

public class TexturedGuiContainer extends GuiContainer {

    private Color color = new Color();
    private Texture texture;
    private float x, y, w, h;

    public TexturedGuiContainer(Texture t, float x, float y, float w, float h) {
	this.texture = t;
	this.x = x;
	this.y = y;
	this.w = w;
	this.h = h;
    }

    public TexturedGuiContainer setX(float x) {
	this.x = x;
	return this;
    }

    public TexturedGuiContainer setY(float y) {
	this.y = y;
	return this;
    }

    public TexturedGuiContainer setW(float w) {
	this.w = w;
	return this;
    }

    public TexturedGuiContainer setH(float h) {
	this.h = h;
	return this;
    }

    public TexturedGuiContainer setTexture(Texture t) {
	this.texture = t;
	return this;
    }

    public Texture getTexture() {
	return texture;
    }

    public float getX() {
	return x;
    }

    public float getY() {
	return y;
    }

    public float getW() {
	return w;
    }

    public float getH() {
	return h;
    }

    public Color getColor() {
	return color;
    }

    @Override
    public void draw(SpriteBatch batch) {
	batch.color().setFrom(color);
	batch.draw(texture, x, y, w, h);
    }

}
