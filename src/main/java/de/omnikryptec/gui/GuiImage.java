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

package de.omnikryptec.gui;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.d2.compat.BorderedBatch2D;
import de.omnikryptec.util.data.Color;

public class GuiImage extends GuiComponentPositionable {
    
    private Texture texture;
    private final Color color = new Color();
    
    @Override
    protected void renderComponent(BorderedBatch2D batch, float aspect) {
        batch.color().set(this.color);
        batch.draw(this.texture, getX(), getY(), getW(), getH());
    }
    
    public void setTexture(Texture t) {
        this.texture = t;
    }
    
    public Color color() {
        return this.color;
    }
    
}
