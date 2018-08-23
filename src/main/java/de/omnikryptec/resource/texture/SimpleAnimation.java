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

import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.resource.loader.ResourceObject;

/**
 * Animations in animations may not work correctly.
 * @author pcfreak9000
 *
 */
public class SimpleAnimation extends Texture {

    private Texture[] textures;
    private int index = 0;
    private float secondsperframe = 1;
    private float time = 0;
    private long lastupdated = 0;

    public SimpleAnimation(float fps, Texture... textures) {
        this("", fps, textures);
    }
    
    public SimpleAnimation(String name, float fps, Texture... textures) {
        super(name, true);
        this.textures = textures;
        this.secondsperframe = 1.0f / fps;
    }

    public SimpleAnimation setFPS(float fps) {
        this.secondsperframe = 1.0f / fps;
        return this;
    }

    public SimpleAnimation setSecondsPerFrame(float f) {
        this.secondsperframe = f;
        return this;
    }

    public int getCurrentIndex() {
        return index;
    }

    public Texture[] getTextures() {
        return textures;
    }

    @Override
    public void bindToUnit(int unit, int... info) {
        textures[index].bindToUnitOptimized(unit, info);
        if (lastupdated < OmniKryptecEngine.instance().getDisplayManager().getFramecount()) {
            lastupdated = OmniKryptecEngine.instance().getDisplayManager().getFramecount();
            if (time >= secondsperframe) {
                index++;
                index %= textures.length;
                time = 0;
            } else {
                time += OmniKryptecEngine.instance().getDeltaTimef();
            }
        }
    }

	@Override
	public float getWidth() {
		return textures[index].getWidth();
	}

	@Override
	public float getHeight() {
		return textures[index].getHeight();
	}

	@Override
	public ResourceObject delete() {
		return this;
	}

}
