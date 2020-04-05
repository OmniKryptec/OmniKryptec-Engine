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

package de.omnikryptec.render.batch.module;

import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;
import de.omnikryptec.util.data.Color;

public class ColorModule implements Module {

    @Override
    public int size() {
        return 4;
    }

    @Override
    public boolean sideIndependant() {
        return true;
    }

    private final Color color = new Color();

    public Color color() {
        return this.color;
    }

    @Override
    public void visit(final float[] array, final QuadSide side, final int index) {
        array[index] = this.color.getR();
        array[index + 1] = this.color.getG();
        array[index + 2] = this.color.getB();
        array[index + 3] = this.color.getA();
    }

}
