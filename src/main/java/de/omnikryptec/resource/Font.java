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

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.util.math.Mathf;

public class Font {
    
    private final FontFile fontFile;
    private final Texture fontAtlas;
    private final boolean sdf;
    
    private final Map<FontCharacter, TextureRegion> cache;
    
    public Font(FontFile fontFile, Texture fontAtlas, boolean sdf) {
        this.cache = new HashMap<>();
        this.fontFile = fontFile;
        this.fontAtlas = fontAtlas;
        this.sdf = sdf;
    }
    
    public float getLength(String text, float size) {
        return getLength(text, size, 1);
    }
    
    public float getLength(String text, float size, float aspectCorrection) {
        char[] chars = text.toCharArray();
        float xOffset = 0;
        for (char c : chars) {
            if (c == ' ') {
                xOffset += getFontFile().getSpaceWidth() * size / aspectCorrection;
            }
            FontCharacter character = getFontFile().getCharacter(c);
            if (character != null) {
                xOffset += character.getCursorAdvanceX() * size / aspectCorrection;
            }
        }
        return xOffset;
    }
    
    public float getHeightBase(String text, float size) {
        return (getFontFile().getBase() - getFontFile().getLineHeight()) * size;
    }
    
    //TODO pcfreak9000 maybe create a "String" like class where some of this information is precomputed? 
    public float getHeightAbs(String text, float size) {
        float f = -100;
        for (char c : text.toCharArray()) {
            FontCharacter k = getFontFile().getCharacter(c);
            if (k != null) {//If it's null it is probably a space then...
                f = Mathf.max(f, k.getOffsetY());
            }
        }
        return (getFontFile().getBase() - getFontFile().getLineHeight() - f) * size;
    }
    
    public boolean isSDFFont() {
        return this.sdf;
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
