package de.omnikryptec.resource;

import de.omnikryptec.libapi.exposed.render.Texture;

public class Font {
    
    private FontFile fontFile;
    private Texture fontAtlas;
    
    public Font(FontFile fontFile, Texture fontAtlas) {
        this.fontFile = fontFile;
        this.fontAtlas = fontAtlas;
    }
    
    public FontFile getFontFile() {
        return fontFile;
    }
    
    public Texture getFontTexture() {
        return fontAtlas;
    }
    
}
