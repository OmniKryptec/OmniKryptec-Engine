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

package de.omnikryptec.old.gui;

import de.omnikryptec.old.graphics.SpriteBatch;
import de.omnikryptec.old.resource.texture.Texture;
import de.omnikryptec.old.util.Color;

public class ProgressBar extends TexturedGuiContainer {

	private Color barcolor = new Color();
	private Texture bartexture;
	private float value = 0;
	
	public ProgressBar(Texture t, Texture b, float x, float y, float w, float h) {
		super(t, x, y,w,h);
		this.bartexture = b;
		
	}

	public ProgressBar setValue(float b) {
		this.value = Math.min(1, Math.max(b, 0));
		return this;
	}

	public ProgressBar setBarTexture(Texture b) {
		this.bartexture = b;
		return this;
	}

	public float getValue() {
		return value;
	}

	public Color getBarColor() {
		return barcolor;
	}
	
	public ProgressBar setIndeterminate() {
		this.value = -1;
		return this;
	}
	
	public boolean isIndeterminate() {
		return this.value == -1;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.color().setFrom(getColor());
		if (getTexture() == null) {
			batch.fillRect(getX(), getY(), getW(), getH());
		} else {
			batch.draw(getTexture(), getX(), getY(), getW(), getH());
		}
		batch.color().setFrom(barcolor);
		if (bartexture == null) {
			batch.fillRect(getX(), getY(), value * getW(), getH());
		} else {
			batch.draw(bartexture, getX(), getY(), value * getW(), getH());
		}
	}

}
