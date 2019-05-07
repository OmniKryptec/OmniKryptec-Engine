package de.omnikryptec.render.batch;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;

//if transform==null use identity transform
public interface Batch2D {
    void begin();
    
    Color color();
    
    void end();

    default void draw(final Texture texture, final Matrix3x2fc transform, final boolean flipU, final boolean flipV) {
        draw(texture, transform, 1f, 1f, flipU, flipV);
    }
    
    void draw(Texture texture, Matrix3x2fc transform, float width, float height, boolean flipU, boolean flipV);
    
    default void drawPolygon(final Texture texture, final float[] poly) {
        drawPolygon(texture, poly, 0, poly.length);
    }
    
    void drawPolygon(Texture texture, float[] poly, int start, int len);
    
    default void drawRect(final Matrix3x2fc transform, final float width, final float height) {
        draw((Texture) null, transform, width, height, false, false);
    }
    
    default void drawLine(final float x0, final float y0, final float x1, final float y1, final float thickness) {
        final float dx = x1 - x0;
        final float dy = y1 - y0;
        final float dist = Mathf.sqrt(dx * dx + dy * dy);
        final float rad = Mathf.arctan2(dx, dy);
        final Matrix3x2f m = new Matrix3x2f();
        m.translate(x0, y0);
        m.rotate(rad);
        drawLine(m, dist, thickness);
    }
    
    default void drawLine(final Matrix3x2fc transform, final float length, final float thickness) {
        drawRect(transform, length, thickness);
    }
}
