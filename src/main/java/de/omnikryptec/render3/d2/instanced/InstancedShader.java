package de.omnikryptec.render3.d2.instanced;

import java.util.stream.IntStream;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.shader.ShaderProgram;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSamplerArray;

public class InstancedShader {
    private final RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
    private ShaderProgram shaderProgram;
    
    private UniformMatrix projViewU;
    
    InstancedShader() {
        shaderProgram = api.createShader();
        shaderProgram.create("defaultInstancedShader");
        projViewU = shaderProgram.getUniform("u_projview");
        UniformSamplerArray samplers = new UniformSamplerArray("samplers",
                InstancedBatch2D.TEXTURE_ACCUM_SIZE);
        samplers.loadSamplerArray(IntStream.range(0, InstancedBatch2D.TEXTURE_ACCUM_SIZE).toArray());
        shaderProgram.bindShader();
        projViewU.loadMatrix(new Matrix4f());
    }
    
    void bindShader() {
        this.shaderProgram.bindShader();
    }
    
    public void setProjectionViewMatrix(Matrix4fc mat) {
        bindShader();
        projViewU.loadMatrix(mat);
    }
}
