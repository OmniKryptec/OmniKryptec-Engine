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

public class AdvancedBatch2D extends AbstractBatch implements Batch2D {

    public static AbstractAdvancedShaderSlot createDefaultShaderSlot() {
        return new AdvancedShaderSlot();
    }

    private PositionModule posModule;
    private UVModule uvModule;
    private ColorModule colorModule;
    private ColorModule reflectionMod;

    private AbstractAdvancedShaderSlot shaderSlot;

    public AdvancedBatch2D(final int vertices) {
        this(vertices, createDefaultShaderSlot());
    }

    public AdvancedBatch2D(final int vertices, final AbstractAdvancedShaderSlot shaderslot) {
        this(new RenderedVertexManager(vertices, shaderslot));
        this.shaderSlot = shaderslot;
    }

    public AdvancedBatch2D(final VertexManager vertexManager) {
        super(vertexManager);
    }

    @Override
    protected ModuleBatchingManager createManager() {
        this.posModule = new PositionModule();
        this.uvModule = new UVModule();
        this.colorModule = new ColorModule();
        this.reflectionMod = new ColorModule();
        return new ModuleBatchingManager(this.colorModule, this.reflectionMod, this.posModule, this.uvModule);
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

    public Color reflectionStrength() {
        return this.reflectionMod.color();
    }

    @Nullable
    public AbstractAdvancedShaderSlot getShaderSlot() {
        return this.shaderSlot;
    }
}