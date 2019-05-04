package de.omnikryptec.render.batch;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.IProjection;

public class ReflectedShaderSlot extends ShaderSlot {
    
    private UniformMatrix viewProjection;
    private UniformMatrix transform;
    
    private IProjection projection;
    
    private Texture reflectionTexture;
    
    public ReflectedShaderSlot() {
        shader.create("engineRenderBatch2DShaderRef");
        this.transform = this.shader.getUniform("u_transform");
        this.viewProjection = this.shader.getUniform("u_projview");
        
        final UniformSampler sampler = this.shader.getUniform("sampler");
        final UniformSampler refl = this.shader.getUniform("reflected");
        this.shader.bindShader();
        this.transform.loadMatrix(new Matrix4f());
        this.viewProjection.loadMatrix(new Matrix4f().ortho2D(0, 1, 0, 1));
        sampler.setSampler(0);
        refl.setSampler(1);
    }
    
    @Override
    protected void onBound() {
        if (projection != null) {
            viewProjection.loadMatrix(projection.getProjection());
        }
        reflectionTexture.bindTexture(1);
    }
    
    public void setReflection(Texture t) {
        this.reflectionTexture = t;
    }
    
    public void setProjection(IProjection projection) {
        this.projection = projection;
    }
    
    public void setViewProjectionMatrix(Matrix4fc mat) {
        shader.bindShader();
        viewProjection.loadMatrix(mat);
    }
    
    public void setTransformMatrix(Matrix4fc mat) {
        shader.bindShader();
        transform.loadMatrix(mat);
    }
}
