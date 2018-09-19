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

package de.omnikryptec.old.main;

import de.omnikryptec.old.gameobject.Camera;
import de.omnikryptec.old.gameobject.GameObject2D;
import de.omnikryptec.old.settings.GameSettings;

public abstract class AbstractScene2D extends AbstractScene<GameObject2D> {

	protected static int cox,coy;

	static {
		cox = OmniKryptecEngine.instance().getGameSettings().getInteger(GameSettings.CHUNK_OFFSET_2D_X);
		coy = OmniKryptecEngine.instance().getGameSettings().getInteger(GameSettings.CHUNK_OFFSET_2D_Y);
	}
	
	protected AbstractScene2D(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}

	@Override
	public final void addGameObject(GameObject2D go, boolean added) {
		super.addGameObject(go, added);
		if (go.hasChilds()) {
			for (GameObject2D g : go.getChilds()) {
				addGameObject(g, added);
			}
		}
	}

	@Override
	public final GameObject2D removeGameObject(GameObject2D go, boolean delete) {
		super.removeGameObject(go, delete);
		if (go.hasChilds()) {
			for (GameObject2D g : go.getChilds()) {
				removeGameObject(g, delete);
			}
		}
		return go;
	}

	
	public static int getCox() {
		return cox;
	}
	
	public static int getCoy() {
		return coy;
	}
}
