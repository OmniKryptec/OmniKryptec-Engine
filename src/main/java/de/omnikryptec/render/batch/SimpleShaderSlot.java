package de.omnikryptec.render.batch;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.shader.UniformBool;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.math.transform.Transform3Df;

public class SimpleShaderSlot extends AbstractProjectedShaderSlot {

    private final UniformMatrix viewProjectionUniform;
    private final UniformMatrix transformUniform;

    private final UniformBool usesTexture;

    private IProjection projection;
    private Transform3Df transform;

    public SimpleShaderSlot() {
        this.shader.create("engineRenderBatch2DShader");
        this.transformUniform = this.shader.getUniform("u_transform");
        this.viewProjectionUniform = this.shader.getUniform("u_projview");
        this.usesTexture = this.shader.getUniform("booleanTexture");

        final UniformSampler sampler = this.shader.getUniform("sampler");
        this.shader.bindShader();
        this.transformUniform.loadMatrix(Mathf.IDENTITY4f);
        this.viewProjectionUniform.loadMatrix(new Matrix4f().ortho2D(0, 1, 0, 1));
        sampler.setSampler(0);
    }

    @Override
    protected void onBound() {
        if (this.projection != null) {
            this.viewProjectionUniform.loadMatrix(this.projection.getProjection());
        }
        if (this.transform != null) {
            this.transformUniform.loadMatrix(this.transform.worldspace());
        }
    }

    @Override
    public void setProjection(final IProjection projection) {
        this.projection = projection;
    }

    public void setViewProjectionMatrix(final Matrix4fc mat) {
        this.shader.bindShader();
        this.viewProjectionUniform.loadMatrix(mat);
    }

    public void setTransformMatrix(final Matrix4fc mat) {
        this.shader.bindShader();
        this.transformUniform.loadMatrix(mat);
    }

    @Override
    public void setTransform(final Transform3Df transform) {
        this.transform = transform;
        if (this.transform == null) {
            setTransformMatrix(Mathf.IDENTITY4f);
        }
    }

    @Override
    public void setNextUsesTexture(boolean b) {
        this.usesTexture.loadBoolean(b);
    }
}
