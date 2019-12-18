package de.omnikryptec.render.batch;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.shader.Shader;

public abstract class AbstractShaderSlot {
    protected final Shader shader;

    public AbstractShaderSlot() {
        this.shader = LibAPIManager.instance().getGLFW().getRenderAPI().createShader();
    }

    protected abstract void onBound();

    public final void bindShaderRenderReady() {
        this.shader.bindShader();
        onBound();
    }

    public void setNextUsesTexture(boolean b) {
    }
}
