package de.omnikryptec.render.batch;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.libapi.exposed.render.shader.UniformVec2;
import de.omnikryptec.libapi.exposed.render.shader.UniformVec4;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.math.transform.Transform3Df;

public class AdvancedShaderSlot extends AbstractAdvancedShaderSlot {
    
    private final UniformMatrix viewProjectionUniform;
    private final UniformMatrix transformUniform;
    
    private final UniformVec2 signedDistanceData;
    private final UniformVec2 sdBorderData;
    private final UniformVec2 offset;
    private final UniformVec4 borderColor;
    
    private IProjection projection;
    private Transform3Df transform;
    
    private Texture reflectionTexture;
    
    public AdvancedShaderSlot() {
        this.shader.create("engineRenderBatch2DShaderRef");
        this.transformUniform = this.shader.getUniform("u_transform");
        this.viewProjectionUniform = this.shader.getUniform("u_projview");
        this.signedDistanceData = this.shader.getUniform("signedDistanceData");
        this.sdBorderData = this.shader.getUniform("borderData");
        this.offset = this.shader.getUniform("borderOffset");
        this.borderColor = this.shader.getUniform("borderColor");
        
        final UniformSampler sampler = this.shader.getUniform("sampler");
        final UniformSampler refl = this.shader.getUniform("reflected");
        this.shader.bindShader();
        this.transformUniform.loadMatrix(Mathf.IDENTITY4f);
        this.viewProjectionUniform.loadMatrix(new Matrix4f().ortho2D(0, 1, 0, 1));
        sampler.setSampler(0);
        refl.setSampler(1);
        signedDistanceData.loadVec2(0, 1);
    }
    
    @Override
    protected void onBound() {
        if (this.projection != null) {
            this.viewProjectionUniform.loadMatrix(this.projection.getProjection());
        }
        if (this.transform != null) {
            this.transformUniform.loadMatrix(this.transform.worldspace());
        }
        this.reflectionTexture.bindTexture(1);
    }
    
    @Override
    public void setReflection(final Texture t) {
        this.reflectionTexture = t;
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
    public void setSignedDistanceData(Vector2fc vec) {
        this.shader.bindShader();
        this.signedDistanceData.loadVec2(vec);
    }
    
    @Override
    public void setSDBorderData(Vector2fc vec) {
        this.shader.bindShader();
        this.sdBorderData.loadVec2(vec);
    }
    
    @Override
    public void setBorderColor(Color color) {
        this.shader.bindShader();
        this.borderColor.loadColor(color);
    }
    
    @Override
    public void setBorderOffset(Vector2fc vec) {
        this.shader.bindShader();
        this.setBorderOffset(vec);
    }
}
