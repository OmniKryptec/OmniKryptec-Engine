package de.omnikryptec.render.batch;

import javax.annotation.Nullable;

import org.joml.Matrix3x2fc;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.util.data.Color;

public class SimpleBatch2D extends AbstractBatch implements Batch2D {
    
    private PositionModule posModule;
    private UVModule uvModule;
    private ColorModule colorModule;
    
    private SimpleShaderSlot shaderSlot;
    
    public SimpleBatch2D(int vertices) {
        this(new RenderedVertexManager(vertices, new SimpleShaderSlot()));
        this.shaderSlot = ((SimpleShaderSlot) ((RenderedVertexManager) vertexManager).getShaderSlot());
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
        posModule.setTransform(transform, width, height);
        uvModule.set(texture, flipU, flipV);
        issueVertices(texture);
        //        if (texture instanceof TextureRegion) {
        //            draw((TextureRegion) texture, transform, width, height, flipU, flipV);
        //        } else {
        //            draw(texture, transform, width, height, flipU, flipV, texture == null ? -1 : 0, texture == null ? -1 : 0,
        //                    texture == null ? -1 : 1, texture == null ? -1 : 1);
        //        }
    }
    
    //    private void draw(final TextureRegion texture, final Matrix3x2fc transform, final float width, final float height,
    //            final boolean flipU, final boolean flipV) {
    //        final float u0 = texture == null ? -1 : texture.u0();
    //        final float v0 = texture == null ? -1 : texture.v0();
    //        final float u1 = texture == null ? -1 : texture.u1();
    //        final float v1 = texture == null ? -1 : texture.v1();
    //        draw(texture == null ? null : texture.getBaseTexture(), transform, width, height, flipU, flipV, u0, v0, u1, v1);
    //    }
    //    
    //    private void draw(final Texture texture, Matrix3x2fc transform, final float width, final float height,
    //            final boolean flipU, boolean flipV, float u0, float v0, float u1, float v1) {
    //        if (texture != null) {
    //            flipV = flipV != texture.requiresInvertedVifDrawn2D();
    //        }
    //        posModule.setTransform(transform, width, height);
    //        uvModule.set(u0, v0, u1, v1, flipV, flipU);
    //        issueVertices(texture);
    //    }
    
    public Color color() {
        return colorModule.color();
    }
    
    @Nullable
    public SimpleShaderSlot getShaderSlot() {
        return shaderSlot;
    }
}
