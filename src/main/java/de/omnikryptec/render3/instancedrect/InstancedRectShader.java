package de.omnikryptec.render3.instancedrect;

import java.util.stream.IntStream;

import org.joml.Matrix4f;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSamplerArray;

public class InstancedRectShader {
    private final RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
    private Shader shader;
    
    InstancedRectShader() {
        shader = api.createShader();
        shader.create("gurke");
        UniformMatrix m = shader.getUniform("u_projview");
        UniformSamplerArray samplers = new UniformSamplerArray("samplers",
                InstancedRectBatchedRenderer.TEXTURE_ACCUM_SIZE);
        samplers.loadSamplerArray(IntStream.range(0, InstancedRectBatchedRenderer.TEXTURE_ACCUM_SIZE).toArray());
        shader.bindShader();
        m.loadMatrix(new Matrix4f());
    }
    
    void bindShader() {
        this.shader.bindShader();
    }
}
