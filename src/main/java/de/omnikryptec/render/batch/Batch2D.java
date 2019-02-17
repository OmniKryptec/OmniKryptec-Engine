package de.omnikryptec.render.batch;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.util.math.Mathf;

public interface Batch2D extends Batch {
    
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
    
    default void drawRect(Matrix3x2fc transform, float width, float height) {
        draw((Texture) null, transform, width, height, false, false);
    }
    
    default void drawLine(float x0, float y0, float x1, float y1, float thickness) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float dist = Mathf.sqrt(dx * dx + dy * dy);
        float rad = Mathf.arctan2(dx, dy);
        Matrix3x2f m = new Matrix3x2f();
        m.translate(x0, y0);
        m.rotate(rad);
        drawLine(m, dist, thickness);
    }
    
    default void drawLine(Matrix3x2fc transform, float length, float thickness) {
        drawRect(transform, length, thickness);
    }
    
    default void drawRect(Matrix3x2fc transform, float width, float height, float thickness) {
        
    }
}
