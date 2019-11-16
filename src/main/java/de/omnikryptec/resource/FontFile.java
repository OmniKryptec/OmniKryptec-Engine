package de.omnikryptec.resource;

import java.util.HashMap;
import java.util.Map;

public class FontFile {
    
    private float spaceWidth;
    private float base;
    private float lineHeight;
    private String name;
    
    private Map<Character, FontCharacter> metaData = new HashMap<>();
    
    public FontFile(Map<Character, FontCharacter> data, float spaceWidth, String name, float base, float lineHeight) {
        this.metaData = data;
        this.spaceWidth = spaceWidth;
        this.name = name;
        this.base = base;
        this.lineHeight = lineHeight;
    }
    
    public String getName() {
        return name;
    }
    
    public float getLineHeight() {
        return lineHeight;
    }
    
    public float getSpaceWidth() {
        return spaceWidth;
    }
    
    public FontCharacter getCharacter(char c) {
        return metaData.get(c);
    }

    public float getBase() {
        return base;
    }
    
}
