package de.omnikryptec.resource;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;

public class Font {
    
    private FontFile fontFile;
    private Texture fontAtlas;
    
    private Map<FontCharacter, TextureRegion> cache;
    
    public Font(FontFile fontFile, Texture fontAtlas) {
        this.cache = new HashMap<>();
        this.fontFile = fontFile;
        this.fontAtlas = fontAtlas;
    }
    
    public FontFile getFontFile() {
        return fontFile;
    }
    
    public TextureRegion getCharacterTexture(FontCharacter c) {
        TextureRegion r = cache.get(c);
        if (r == null) {
            r = new TextureRegion(getFontTexture(), c.getTextureCoordX(), c.getTextureCoordY(), c.getTextureCoordMaxX(),
                    c.getTextureCoordMaxY());
            cache.put(c, r);
        }
        return r;
    }
    
    public Texture getFontTexture() {
        return fontAtlas;
    }
    
}
