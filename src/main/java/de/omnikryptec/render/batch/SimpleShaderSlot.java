package de.omnikryptec.render.batch;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.IProjection;

public class SimpleShaderSlot extends ShaderSlot {
    
    private UniformMatrix viewProjection;
    private UniformMatrix transform;
    
    private IProjection projection;
    
    public SimpleShaderSlot() {
        shader.create("engineRenderBatch2DShader");
        this.transform = this.shader.getUniform("u_transform");
        this.viewProjection = this.shader.getUniform("u_projview");
        
        final UniformSampler sampler = this.shader.getUniform("sampler");
        this.shader.bindShader();
        this.transform.loadMatrix(new Matrix4f());
        this.viewProjection.loadMatrix(new Matrix4f().ortho2D(0, 1, 0, 1));
        sampler.setSampler(0);
    }
    
    @Override
    protected void onBound() {
        if (projection != null) {
            viewProjection.loadMatrix(projection.getProjection());
        }
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
