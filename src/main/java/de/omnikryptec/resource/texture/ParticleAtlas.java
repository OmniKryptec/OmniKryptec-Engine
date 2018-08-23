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

package de.omnikryptec.resource.texture;

import de.omnikryptec.util.EnumCollection.BlendMode;

public class ParticleAtlas {
	private int numberOfRows;
	private BlendMode blenddmode;
	private Texture tex;

	public ParticleAtlas(Texture t, int numberOfRows, BlendMode blenddmode) {
		this.tex = t;
		this.numberOfRows = numberOfRows;
		this.blenddmode = blenddmode;
	}

	public BlendMode getBlendMode() {
		return blenddmode;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public ParticleAtlas setBlendMode(BlendMode b) {
		this.blenddmode = b;
		return this;
	}

	public ParticleAtlas setNumberOfRows(int i) {
		this.numberOfRows = i;
		return this;
	}

	public Texture getTexture() {
		return tex;
	}

	public ParticleAtlas setTexture(Texture t) {
		this.tex = t;
		return this;
	}

}
