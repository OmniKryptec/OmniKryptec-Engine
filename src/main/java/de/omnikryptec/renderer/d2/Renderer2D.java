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

package de.omnikryptec.renderer.d2;

import java.util.Comparator;
import java.util.HashMap;

import de.omnikryptec.gameobject.Sprite;
import de.omnikryptec.main.AbstractScene2D;
import de.omnikryptec.main.ChunkCoord2D;

public interface Renderer2D {
	
	public static final Comparator<Sprite> LAYER_COMPARATOR = new Comparator<Sprite>() {

		@Override
		public int compare(Sprite o1, Sprite o2) {
			return (int) Math.signum(o1.getLayer()-o2.getLayer());
		}
	};
	
	long render(AbstractScene2D sc, RenderChunk2D global, long camChunkX, long camChunkY, int chunkOffsetX, int chunkOffsetY,
			HashMap<ChunkCoord2D, RenderChunk2D> scene);

	default Comparator<Sprite> getLayerComparator(){
		return LAYER_COMPARATOR;
	}
}
