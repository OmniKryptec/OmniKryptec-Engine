package de.omnikryptec.render.batch;

import org.joml.Matrix3x2fc;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.util.data.Color;

public class GuiBatch implements Batch2D {

    @Override
    public void begin() {
    }

    @Override
    public Color color() {
        
        return null;
    }

    @Override
    public void end() {
    }

    public void drawString(String s) {
        
    }
    
    @Override
    public void draw(Texture texture, Matrix3x2fc transform, float width, float height, boolean flipU, boolean flipV) {
    }

    @Override
    public void drawPolygon(Texture texture, float[] poly, int start, int len) {
    }
    
}
