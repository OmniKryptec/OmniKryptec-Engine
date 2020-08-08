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

package de.omnikryptec.render.batch;

import org.joml.Vector2f;

import de.omnikryptec.resource.Font;
import de.omnikryptec.util.data.Color;

public interface BorderedBatch2D extends Batch2D {

    Color borderColor();

    Vector2f borderSDFData();

    Vector2f borderOffset();

    Vector2f signedDistanceFieldData();

    default void setDefaultSDFData() {
        signedDistanceFieldData().set(0, 1);
    }

    default void setDefaultBDSFData() {
        borderSDFData().set(0, 0.00000000001f);
    }

    default void setDefaultBorderOffset() {
        borderOffset().set(0);
    }

    default void drawStringSDFautoc(String string, Font font, float size, float thickness, float x, float y,
            float rad) {
        this.drawStringSDFautoc(string, font, size, 1, thickness, x, y, rad);
    }

    default void drawStringSDFautoc(String string, Font font, float size, float aspect, float thickness, float x,
            float y, float rad) {
        if (!font.isSDFFont()) {
            drawStringSimple(string, font, size, aspect, x, y, rad);
        } else {
            float dif = 1 / size * 0.01f;
            float bx = signedDistanceFieldData().x;
            float by = signedDistanceFieldData().y;
            signedDistanceFieldData().set(thickness, thickness + dif);
            drawStringSimple(string, font, size, aspect, x, y, rad);
            signedDistanceFieldData().set(bx, by);
        }
    }
}
