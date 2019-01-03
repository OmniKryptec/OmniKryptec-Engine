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

package de.omnikryptec.old.gui.rendering;

import de.omnikryptec.old.gameobject.Camera;
import de.omnikryptec.old.graphics.SpriteBatch;
import de.omnikryptec.old.gui.GuiContainer;

public class GuiRenderer {

    private SpriteBatch batch;
    private GuiContainer parent;

    public GuiRenderer() {
	batch = new SpriteBatch(new Camera().setOrthographicProjection2D(0, 0, 1, 1));
    }

    public void paint() {
	if (parent != null) {
	    batch.begin();
	    parent.update(batch, true);
	    batch.end();
	}
    }

    public void setGui(GuiContainer gui) {
	this.parent = gui;
    }
}
