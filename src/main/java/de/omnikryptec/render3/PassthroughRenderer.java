package de.omnikryptec.render3;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.shader.ShaderProgram;
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
    
    private ShaderProgram shaderProgram;
    
    private PassthroughRenderer() {
        RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
        shaderProgram = api.createShader();
        shaderProgram.create("pp-vert", "passthrough");
        UniformSampler sampler = shaderProgram.getUniform("img");
        shaderProgram.bindShader();
        sampler.setSampler(0);
    }
    
    public void render(Texture tex) {
        shaderProgram.bindShader();
        tex.bindTexture(0);
        QuadMesh.renderScreenQuad();
    }
}
