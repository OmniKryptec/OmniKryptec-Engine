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

package de.omnikryptec.resource;

import java.util.HashMap;
import java.util.Map;

public class FontFile {

    private final float spaceWidth;
    private final float base;
    private final float lineHeight;
    private final String name;

    private Map<Character, FontCharacter> metaData = new HashMap<>();

    public FontFile(Map<Character, FontCharacter> data, float spaceWidth, String name, float base, float lineHeight) {
        this.metaData = data;
        this.spaceWidth = spaceWidth;
        this.name = name;
        this.base = base;
        this.lineHeight = lineHeight;
    }

    public String getName() {
        return this.name;
    }

    public float getLineHeight() {
        return this.lineHeight;
    }

    public float getSpaceWidth() {
        return this.spaceWidth;
    }

    public FontCharacter getCharacter(char c) {
        return this.metaData.get(c);
    }

    public float getBase() {
        return this.base;
    }

    public float getWidth(String s, float size) {
        char[] chars = s.toCharArray();
        float w = 0;
        for (char c : chars) {
            if (c == ' ') {
                w += getSpaceWidth();
            }
            FontCharacter character = getCharacter(c);
            if (character != null) {
                w += character.getOffsetX();
                w += character.getCursorAdvanceX();
            }
        }
        return size * w;
    }
}
