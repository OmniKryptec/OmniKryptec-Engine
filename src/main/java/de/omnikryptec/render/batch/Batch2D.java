package de.omnikryptec.render.batch;

import org.joml.Matrix3fc;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.util.data.Color;

public interface Batch2D extends Batch {
    /*
     * default void drawTexture(Texture texture, float x, float y) {
     * drawTexture(texture, x, y, texture.getWidth(), texture.getWidth()); }
     * 
     * void drawTexture(Texture texture, float x, float y, float width, float
     * height);
     */
    
    void draw(Texture texture, Matrix3fc transform, boolean flipU, boolean flipV);
    
    void draw(TextureRegion texture, Matrix3fc transform, boolean flipU, boolean flipV);
    
    default void drawPolygon(Texture texture, float[] poly) {
        drawPolygon(texture, poly, 0, poly.length);
    }
    
    void drawPolygon(Texture texture, float[] poly, int start, int len);
    
}
