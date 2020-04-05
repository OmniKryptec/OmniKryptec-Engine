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

import org.joml.Vector2f;

import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;

public class SDFModule implements Module {

    private final Vector2f sdData = new Vector2f();
    private final Vector2f bsdData = new Vector2f();

    public SDFModule() {
        setDefaultSD();
        setDefaultBSD();
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public boolean sideIndependant() {
        return true;
    }

    @Override
    public void visit(float[] array, QuadSide side, int index) {
        array[index] = this.sdData.x();
        array[index + 1] = this.sdData.y();
        array[index + 2] = this.bsdData.x();
        array[index + 3] = this.bsdData.y();
    }

    public Vector2f sdfData() {
        return this.sdData;
    }

    public Vector2f bsdfData() {
        return this.bsdData;
    }

    public void setDefaultSD() {
        this.sdData.set(0, 1);
    }

    public void setDefaultBSD() {
        this.bsdData.set(0, 0.000000001f);
    }

}
