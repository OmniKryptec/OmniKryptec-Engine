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
    
    public static AbstractProjectedShaderSlot createDefaultShaderSlot() {
        return new SimpleShaderSlot();
    }
    
    private PositionModule posModule;
    private UVModule uvModule;
    private ColorModule colorModule;
    
    private AbstractProjectedShaderSlot shaderSlot;
    
    public SimpleBatch2D(final int vertices) {
        this(vertices, createDefaultShaderSlot());
    }
    
    public SimpleBatch2D(final int vertices, final AbstractProjectedShaderSlot shaderslot) {
        this(new RenderedVertexManager(vertices, shaderslot));
        this.shaderSlot = shaderslot;
    }
    
    public SimpleBatch2D(final VertexManager vertexManager) {
        super(vertexManager);
    }
    
    @Override
    protected ModuleBatchingManager createManager() {
        this.posModule = new PositionModule();
        this.uvModule = new UVModule();
        this.colorModule = new ColorModule();
        return new ModuleBatchingManager(this.colorModule, this.posModule, this.uvModule);
    }
    
    @Override
    public void draw(final Texture texture, final Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, final boolean flipV) {
        this.posModule.setTransform(transform, width, height);
        this.uvModule.set(texture, flipU, flipV);
        issueVertices(texture);
    }
    
    @Override
    public Color color() {
        return this.colorModule.color();
    }
    
    @Nullable
    public AbstractProjectedShaderSlot getShaderSlot() {
        return this.shaderSlot;
    }
}
