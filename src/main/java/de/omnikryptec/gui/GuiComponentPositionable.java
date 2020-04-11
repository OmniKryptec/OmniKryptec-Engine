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

import de.omnikryptec.util.math.Mathf;

public class GuiComponentPositionable extends GuiComponent {
    
    private float mx, my, mw, mh;
    private float x, y, w, h;
    
    private boolean alwaysMax = true;
    
    @Override
    protected void calculateActualPosition(GuiConstraints constraints) {
        this.mx = constraints.getX();
        this.my = constraints.getY();
        this.mw = constraints.getMaxWidth();
        this.mh = constraints.getMaxHeight();
    }
    
    public float getX() {
        return this.alwaysMax ? this.mx : this.mx + x * mw;
    }
    
    public float getY() {
        return this.alwaysMax ? this.my : this.my + y * mh;
    }
    
    public float getW() {
        return this.alwaysMax ? this.mw : Mathf.min(mw * w, mw * (1 - x));
    }
    
    public float getH() {
        return this.alwaysMax ? this.mh : Mathf.min(mh * h, mh * (1 - y));
    }
    
    public void setDimensions(float x, float y, float w, float h) {
        setMaxAlways(false);
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    public void setMaxAlways() {
        setMaxAlways(true);
    }
    
    private void setMaxAlways(boolean b) {
        this.alwaysMax = b;
    }
    
}
