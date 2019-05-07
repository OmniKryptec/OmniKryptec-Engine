package de.omnikryptec.render.batch;

import javax.annotation.Nullable;

import org.joml.Matrix3x2fc;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.module.ColorModule;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;
import de.omnikryptec.render.batch.module.PositionModule;
import de.omnikryptec.render.batch.module.UVModule;
import de.omnikryptec.render.batch.vertexmanager.RenderedVertexManager;
import de.omnikryptec.render.batch.vertexmanager.VertexManager;
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
    }
    
    public Color color() {
        return colorModule.color();
    }
    
    @Nullable
    public SimpleShaderSlot getShaderSlot() {
        return shaderSlot;
    }
}
