package de.omnikryptec.render.batch;

import org.joml.Matrix3x2fc;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.util.data.Color;

public class SimpleBatch2D extends AbstractBatch implements Batch2D {
    
    private PositionModule posModule;
    private UVModule uvModule;
    private ColorModule colorModule;
    
    public SimpleBatch2D(int vertices) {
        this(new RenderedVertexManager(vertices));
    }
    
    public SimpleBatch2D(VertexManager vertexManager) {
        super(vertexManager);
    }
    
    @Override
    protected ModuleBatchingManager createManager() {
        posModule = new PositionModule();
        uvModule = new UVModule();
        colorModule = new ColorModule();
        return new ModuleBatchingManager(colorModule, posModule, uvModule);
    }
    
    public void draw(final Texture texture, final Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, final boolean flipV) {
        if (texture instanceof TextureRegion) {
            draw((TextureRegion) texture, transform, width, height, flipU, flipV);
        } else {
            draw(texture, transform, width, height, flipU, flipV, texture == null ? -1 : 0, texture == null ? -1 : 0,
                    texture == null ? -1 : 1, texture == null ? -1 : 1);
        }
    }
    
    private void draw(final TextureRegion texture, final Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, final boolean flipV) {
        final float u0 = texture == null ? -1 : texture.u0();
        final float v0 = texture == null ? -1 : texture.v0();
        final float u1 = texture == null ? -1 : texture.u1();
        final float v1 = texture == null ? -1 : texture.v1();
        draw(texture == null ? null : texture.getBaseTexture(), transform, width, height, flipU, flipV, u0, v0, u1, v1);
    }
    
    private void draw(final Texture texture, Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, boolean flipV, float u0, float v0, float u1, float v1) {
        if (texture != null) {
            flipV = flipV != texture.requiresInvertedVifDrawn2D();
        }
        posModule.setTransform(transform, width, height);
        uvModule.set(u0, v0, u1, v1, flipV, flipU);
        issueVertices(texture);
    }
    
    public Color color() {
        return colorModule.color();
    }
    
    @Override
    public void drawPolygon(Texture texture, float[] poly, int start, int len) {
        issuePreComputed(texture, poly, start, len);
    }
    
}
