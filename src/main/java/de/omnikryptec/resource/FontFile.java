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
