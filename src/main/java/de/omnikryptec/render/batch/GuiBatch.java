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

    public void drawString(final String s) {

    }

    @Override
    public void draw(final Texture texture, final Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, final boolean flipV) {
    }

    @Override
    public void drawPolygon(final Texture texture, final float[] poly, final int start, final int len) {
    }

}
