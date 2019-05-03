package de.omnikryptec.render.batch;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.shader.Shader;

public abstract class ShaderSlot {
    protected final Shader shader;
    
    public ShaderSlot() {
        this.shader = LibAPIManager.instance().getGLFW().getRenderAPI().createShader();
    }
    
    protected abstract void onBound();
    
    public final void bindShaderRenderReady() {
        shader.bindShader();
        onBound();
    }
}
