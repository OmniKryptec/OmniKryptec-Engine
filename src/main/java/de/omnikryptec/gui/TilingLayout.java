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

import java.util.List;

public class TilingLayout implements GuiLayout {
    
    private final int columns;//Spalten
    private final int rows;//Zeilen
    
    public TilingLayout(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
    }
    
    @Override
    public void doLayout(final GuiComponent parent, final List<GuiComponent> children) {
        if (children.size() > this.rows * this.columns) {
            throw new IllegalStateException("not enough space");
        }
        final float width = parent.getConstraints().getMaxWidth() / this.columns;
        final float height = parent.getConstraints().getMaxWidth() / this.rows;
        for (int i = 0; i < children.size(); i++) {
            final int xIndex = i % this.columns;
            final int yIndex = i / this.rows;
            final float x = xIndex * width + parent.getConstraints().getX();
            final float y = yIndex * height + parent.getConstraints().getY();
            final GuiConstraints childsConstraints = new GuiConstraints(x, y, width, height);
            children.get(i).setConstraints(childsConstraints);
        }
    }
    
}
