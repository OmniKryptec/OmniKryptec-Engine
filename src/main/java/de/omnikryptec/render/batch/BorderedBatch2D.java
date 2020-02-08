package de.omnikryptec.render.batch;

import org.joml.Vector2f;

import de.omnikryptec.resource.Font;
import de.omnikryptec.util.data.Color;

public interface BorderedBatch2D extends Batch2D {
    Color borderColor();
    
    Vector2f borderSDFData();
    
    Vector2f borderOffset();
    
    Vector2f signedDistanceFieldData();
    
    default void setDefaultSDFData() {
        signedDistanceFieldData().set(0, 1);
    }
    
    default void setDefaultBDSFData() {
        borderSDFData().set(0, 0.00000000001f);
    }
    
    default void setDefaultBorderOffset() {
        borderOffset().set(0);
    }
    
    default void drawStringSDFautoc(String string, Font font, float size, float thickness, float x, float y,
            float rad) {
        if (!font.isSDFFont()) {
            drawStringSimple(string, font, size, x, y, rad);
        } else {
            float dif = 1 / size * 0.01f;
            float bx = signedDistanceFieldData().x;
            float by = signedDistanceFieldData().y;
            signedDistanceFieldData().set(thickness, thickness + dif);
            drawStringSimple(string, font, size, x, y, rad);
            signedDistanceFieldData().set(bx, by);
        }
    }
}
