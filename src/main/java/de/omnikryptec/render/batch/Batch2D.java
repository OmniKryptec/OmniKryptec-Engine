package de.omnikryptec.render.batch;

import org.joml.Matrix3x2fc;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;

public interface Batch2D extends Batch {
    /*
     * default void drawTexture(Texture texture, float x, float y) {
     * drawTexture(texture, x, y, texture.getWidth(), texture.getWidth()); }
     * 
     * void drawTexture(Texture texture, float x, float y, float width, float
     * height);
     */
    
    default void draw(Texture texture, Matrix3x2fc transform, boolean flipU, boolean flipV) {
        draw(texture, transform, 1f, 1f, flipU, flipV);
    }
    
    void draw(Texture texture, Matrix3x2fc transform, float width, float height, boolean flipU, boolean flipV);
    
    default void draw(TextureRegion texture, Matrix3x2fc transform, boolean flipU, boolean flipV) {
        draw(texture, transform, 1f, 1f, flipU, flipV);
    }
    
    void draw(TextureRegion texture, Matrix3x2fc transform, float width, float height, boolean flipU, boolean flipV);
    
    default void drawPolygon(Texture texture, float[] poly) {
        drawPolygon(texture, poly, 0, poly.length);
    }
    
    void drawPolygon(Texture texture, float[] poly, int start, int len);
    
}
