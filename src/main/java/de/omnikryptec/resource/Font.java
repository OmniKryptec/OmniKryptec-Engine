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

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class Font {

    private final FontFile fontFile;
    private final Texture fontAtlas;

    private final Map<FontCharacter, TextureRegion> cache;

    public Font(FontFile fontFile, Texture fontAtlas) {
        this.cache = new HashMap<>();
        this.fontFile = fontFile;
        this.fontAtlas = fontAtlas;
    }

    public FontFile getFontFile() {
        return this.fontFile;
    }

    public TextureRegion getCharacterTexture(FontCharacter c) {
        TextureRegion r = this.cache.get(c);
        if (r == null) {
            r = new TextureRegion(getFontTexture(), c.getTextureCoordX(), c.getTextureCoordY(), c.getTextureCoordMaxX(),
                    c.getTextureCoordMaxY());
            this.cache.put(c, r);
        }
        return r;
    }

    public Texture getFontTexture() {
        return this.fontAtlas;
    }

}
