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

package de.omnikryptec.old.d2;

import org.joml.Rectanglef;

public class Rectangle extends Rectanglef {

    private float w, h, oldx, oldy;
    private boolean dynamic = false;
    private boolean iscolliding = false;

    public Rectangle() {
	this(0, 0, 0, 0);
    }

    public Rectangle(float x, float y, float w, float h) {
	super(x, y, x + w, y + h);
	this.w = w;
	this.h = h;
	oldx = x;
	oldy = y;
    }

    public float getWidth() {
	return w;
    }

    public float getHeight() {
	return h;
    }

    public Rectangle setWidth(float w) {
	maxX = minX + w;
	return this;
    }

    public Rectangle setHeight(float h) {
	maxY = minY + h;
	return this;
    }

    public Rectangle setPosition(float x, float y) {
	minX = x;
	minY = y;
	maxX = x + w;
	maxY = y + h;
	return this;
    }

    public void saveOld() {
	oldx = minX;
	oldy = minY;
    }

    public void restore() {
	setPosition(oldx, oldy);
    }

    public Rectangle setDynamic(boolean b) {
	this.dynamic = b;
	return this;
    }

    public boolean isDynamic() {
	return dynamic;
    }

    Rectangle setColliding(boolean b) {
	iscolliding = b;
	return this;
    }

    public boolean isColliding() {
	return iscolliding;
    }

    @Override
    public String toString() {
	return "x: " + minX + " y: " + minY + " w: " + w + "  h: " + h;
    }

}
