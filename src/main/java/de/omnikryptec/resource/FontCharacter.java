package de.omnikryptec.resource;

public class FontCharacter {
    private int id;
    private float xTextureCoord;
    private float yTextureCoord;
    private float xMaxTextureCoord;
    private float yMaxTextureCoord;
    private float xOffset;
    private float yOffset;
    private float sizeX;
    private float sizeY;
    private float xAdvance;
    
    /**
     * @param id            - the ASCII value of the character.
     * @param xTextureCoord - the x texture coordinate for the top left corner of
     *                      the character in the texture atlas.
     * @param yTextureCoord - the y texture coordinate for the top left corner of
     *                      the character in the texture atlas.
     * @param xTexSize      - the width of the character in the texture atlas.
     * @param yTexSize      - the height of the character in the texture atlas.
     * @param xOffset       - the x distance from the curser to the left edge of the
     *                      character's quad.
     * @param yOffset       - the y distance from the curser to the top edge of the
     *                      character's quad.
     * @param sizeX         - the width of the character's quad in screen space.
     * @param sizeY         - the height of the character's quad in screen space.
     * @param xAdvance      - how far in pixels the cursor should advance after
     *                      adding this character.
     */
    protected FontCharacter(int id, float xTextureCoord, float yTextureCoord, float xTexSize, float yTexSize,
            float xOffset, float yOffset, float sizeX, float sizeY, float xAdvance) {
        this.id = id;
        this.xTextureCoord = xTextureCoord;
        this.yTextureCoord = yTextureCoord;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.xMaxTextureCoord = xTexSize + xTextureCoord;
        this.yMaxTextureCoord = yTexSize + yTextureCoord;
        this.xAdvance = xAdvance;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    protected int getAscii() {
        return id;
    }
    
    public float getTextureCoordX() {
        return xTextureCoord;
    }
    
    public float getTextureCoordY() {
        return yTextureCoord;
    }
    
    public float getTextureCoordMaxX() {
        return xMaxTextureCoord;
    }
    
    public float getTextureCoordMaxY() {
        return yMaxTextureCoord;
    }
    
    public float getOffsetX() {
        return xOffset;
    }
    
    public float getOffsetY() {
        return yOffset;
    }
    
    public float getSizeX() {
        return sizeX;
    }
    
    public float getSizeY() {
        return sizeY;
    }
    
    public float getCursorAdvanceX() {
        return xAdvance;
    }
    
}
