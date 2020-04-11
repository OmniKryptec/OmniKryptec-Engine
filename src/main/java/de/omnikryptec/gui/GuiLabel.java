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

import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.resource.Font;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.data.Color;

public class GuiLabel extends GuiComponentPositionable {
    
    private Color color;
    private Font font;
    private String text;
    private float thickness;
    private float size;
    private boolean centered;
    
    public GuiLabel() {
        this.color = new Color();
        this.text = "";
        this.thickness = 0.57f;
        this.size = 0.02f;
    }
    
    @Override
    protected void renderComponent(BorderedBatch2D batch, float aspect) {
        batch.color().set(color);
        float x = getX();
        float y = getY();
        if (centered) {
            x += getW() / 2 - font.getLength(text, size, aspect) / 2;
            y += getH() / 2 - font.getHeightAbs(text, size) / 2;
        }
        batch.drawStringSDFautoc(text, font, size, aspect, thickness, x, y, 0);
    }
    
    public Color color() {
        return color;
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setFont(Font font) {
        this.font = Util.ensureNonNull(font);
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = Util.ensureNonNull(text);
    }
    
    public float getThickness() {
        return thickness;
    }
    
    public void setThickness(float thickness) {
        this.thickness = thickness;
    }
    
    public float getSize() {
        return size;
    }
    
    public void setSize(float size) {
        this.size = size;
    }
    
    public boolean isCentered() {
        return centered;
    }
    
    public void setCentered(boolean centered) {
        this.centered = centered;
    }
    
}
