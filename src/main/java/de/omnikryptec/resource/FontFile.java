package de.omnikryptec.resource;

import java.util.HashMap;
import java.util.Map;

public class FontFile {
    
    private double spaceWidth;
    
    private Map<Integer, FontCharacter> metaData = new HashMap<>();
    
    public FontFile(Map<Integer, FontCharacter> data, double spaceWidth) {
        this.metaData = data;
        this.spaceWidth = spaceWidth;
    }
    
    protected double getSpaceWidth() {
        return spaceWidth;
    }
    
    protected FontCharacter getCharacter(int ascii) {
        return metaData.get(ascii);
    }
    
}
