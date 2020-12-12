package de.omnikryptec.render3;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.postprocessing.QuadMesh;

public class PassthroughRenderer {
    
    private static PassthroughRenderer instance;
    
    public static PassthroughRenderer instance() {
        if (instance == null) {
            instance = new PassthroughRenderer();
        }
        return instance;
    }
    
    private Shader shader;
    
    private PassthroughRenderer() {
        RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
        shader = api.createShader();
        shader.create("pp-vert", "passthrough");
        UniformSampler sampler = shader.getUniform("img");
        shader.bindShader();
        sampler.setSampler(0);
    }
    
    public void render(Texture tex) {
        shader.bindShader();
        tex.bindTexture(0);
        QuadMesh.renderScreenQuad();
    }
}
