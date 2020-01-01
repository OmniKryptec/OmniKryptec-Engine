/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import java.util.ArrayList;
import java.util.List;

public abstract class GuiContainer {

    private boolean enabled = true;
    private List<GuiContainer> objs = new ArrayList<>();

    public void update(SpriteBatch batch, boolean checkMouse) {
	if (isEnabled()) {
	    draw(batch);
	    for (GuiContainer g : objs) {
		g.update(batch, checkMouse);
	    }
	}
    }

    public void draw(SpriteBatch batch) {

    }

    public GuiContainer add(GuiContainer g) {
	objs.add(g);
	return this;
    }

    public GuiContainer setEnabled(boolean enabled) {
	this.enabled = enabled;
	return this;
    }

    public boolean isEnabled() {
	return enabled;
    }

}
