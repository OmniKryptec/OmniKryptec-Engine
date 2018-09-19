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

public class Light2D extends Sprite {

    public Light2D() {
        this("", null, null);
    }

    public Light2D(String name, Texture t) {
        this(name, t, null);
    }

    public Light2D(Texture t, GameObject2D p) {
        this("", t, p);
    }

    public Light2D(String name, GameObject2D p) {
        this(name, null, p);
    }

    public Light2D(String name, Texture texture, GameObject2D parent) {
        super(name, texture, parent);
    }

    @Override
    public void paint(SpriteBatch batch) {
        batch.draw(this, true);
    }
}
