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

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.resource.Font;
import de.omnikryptec.resource.FontCharacter;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;

//if transform==null use identity transform
public interface Batch2D {

    void begin();

    Color color();

    void end();

    default void draw(final Texture texture, final Matrix3x2fc transform, final boolean flipU, final boolean flipV) {
        draw(texture, transform, 1f, 1f, flipU, flipV);
    }

    default void draw(final Texture texture, final Matrix3x2fc transform, float width, float height) {
        draw(texture, transform, width, height, false, false);
    }

    default void draw(final Texture texture, final float x, final float y, final boolean flipU, final boolean flipV) {
        draw(texture, x, y, 1f, 1f, flipU, flipV);
    }

    default void draw(final Texture texture, final float x, final float y, float width, float height) {
        draw(texture, x, y, width, height, false, false);
    }

    void draw(Texture texture, Matrix3x2fc transform, float width, float height, boolean flipU, boolean flipV);

    void draw(Texture texture, float x, float y, float width, float height, boolean flipU, boolean flipV);

    default void drawPolygon(final Texture texture, final float[] poly) {
        drawPolygon(texture, poly, 0, poly.length);
    }

    void drawPolygon(Texture texture, float[] poly, int start, int len);

    default void drawRect(final float x, final float y, final float width, final float height) {
        draw((Texture) null, x, y, width, height, false, false);
    }

    default void drawRect(final Matrix3x2fc transform, final float width, final float height) {
        draw((Texture) null, transform, width, height, false, false);
    }

    default void drawLine(final float x0, final float y0, final float x1, final float y1, final float thickness) {
        final float dx = x1 - x0;
        final float dy = y1 - y0;
        final float dist = Mathf.sqrt(dx * dx + dy * dy);
        final float rad = Mathf.arctan2(dy, dx);
        final Matrix3x2f m = new Matrix3x2f();
        m.translate(x0, y0);
        m.rotate(rad);
        drawLine(m, dist, thickness);
    }

    default void drawLine(final Matrix3x2fc transform, final float length, final float thickness) {
        drawRect(transform, length, thickness);
    }

    default void drawStringSimple(String string, Font font, float size, float x, float y, float rad) {
        this.drawStringSimple(string, font, size, 1, x, y, rad);
    }

    default void drawStringSimple(String string, Font font, float size, float aspectCorrection, float x, float y,
            float rad) {
        char[] chars = string.toCharArray();
        //Doing everything in one Matrix and translating it after each char does not work because the floating point error gets too big
        Matrix3x2f baseTranslation = new Matrix3x2f().translation(x, y);
        if (rad != 0) {
            baseTranslation = baseTranslation.rotate(rad);
        }
        Matrix3x2f translation = new Matrix3x2f();
        float xOffset = 0;
        float xVal = 0, yVal = 0;
        for (char c : chars) {
            if (c == ' ') {
                xOffset += font.getFontFile().getSpaceWidth() * size / aspectCorrection;
            }
            FontCharacter character = font.getFontFile().getCharacter(c);
            if (character != null) {
                xVal = character.getOffsetX() * size / aspectCorrection + xOffset;
                yVal = (font.getFontFile().getBase() - character.getSizeY() - character.getOffsetY()) * size;
                draw(font.getCharacterTexture(character), translation.set(baseTranslation).translate(xVal, yVal),
                        character.getSizeX() * size / aspectCorrection, character.getSizeY() * size);
                xOffset += character.getCursorAdvanceX() * size / aspectCorrection;
            }
        }
    }
}
