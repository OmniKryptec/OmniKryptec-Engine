package de.omnikryptec.resource;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;

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
