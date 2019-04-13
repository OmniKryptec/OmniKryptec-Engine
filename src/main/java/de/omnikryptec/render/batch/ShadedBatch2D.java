package de.omnikryptec.render.batch;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.IProjection;

public class ShadedBatch2D extends RenderBatch2D {
    
    public static final String SHADER_NAME = "engineRenderBatch2DShader";
    
    private Shader shader;
    private UniformMatrix viewProjection;
    private UniformMatrix transform;
    
    private IProjection projection;
    
    public ShadedBatch2D(int vertices) {
        super(vertices);
        this.shader = RenderAPI.get().createShader();
        this.shader.create(SHADER_NAME);
        this.transform = this.shader.getUniform("u_transform");
        this.viewProjection = this.shader.getUniform("u_projview");
        
        final UniformSampler sampler = this.shader.getUniform("sampler");
        this.shader.bindShader();
        this.transform.loadMatrix(new Matrix4f());
        this.viewProjection.loadMatrix(new Matrix4f().ortho2D(0, 1, 0, 1));
        sampler.setSampler(0);
    }
    
    @Override
    public void begin() {
        super.begin();
        shader.bindShader();
        if (projection != null) {
            setViewProjection(projection.getProjection());
        }
    }
    
    public void setViewProjection(Matrix4fc vp) {
        if (!isRendering()) {
            shader.bindShader();
        }
        viewProjection.loadMatrix(vp);
    }
    
    public void setGlobalTransform(Matrix4fc transm) {
        if (!isRendering()) {
            shader.bindShader();
        }
        transform.loadMatrix(transm);
    }
    
    //When set this will override the viewproj on #begin
    public void setIProjection(IProjection proj) {
        this.projection = proj;
    }
    
}
