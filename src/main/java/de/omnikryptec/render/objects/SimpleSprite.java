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

package de.omnikryptec.render.objects;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.d2.compat.Batch2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.transform.Transform2Df;

public class SimpleSprite extends Sprite {
    
    private Transform2Df transform = new Transform2Df();
    private float width = 1;
    private float height = 1;
    
    private Color color;
    private Texture texture;
    
    private float tiling = 1;
    
    @Override
    public void draw(final Batch2D batch) {
        batch.color().set(this.color == null ? Color.ONE : this.color);
        batch.draw(this.texture, this.transform.worldspace(), this.width, this.height, false, false);
    }
    
    public Transform2Df getTransform() {
        return this.transform;
    }
    
    public float getTilingFactor() {
        return this.tiling;
    }
    
    public void setTilingFactor(float f) {
        this.tiling = f;
    }
    
    public void setTransform(final Transform2Df mat) {
        this.transform = mat;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public void setHeight(final float height) {
        this.height = height;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(final Color color) {
        this.color = color;
    }
    
    public void setTexture(final Texture tex) {
        this.texture = tex;
    }
    
    public Texture getTexture() {
        return this.texture;
    }
    
}
