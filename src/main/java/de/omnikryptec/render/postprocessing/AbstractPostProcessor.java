package de.omnikryptec.render.postprocessing;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.shader.Shader;

public abstract class AbstractPostProcessor implements Postprocessor{
    
    protected FrameBuffer buffer;
    protected Shader shader;
    
    public AbstractPostProcessor() {
        this.buffer = LibAPIManager.instance().getGLFW().getRenderAPI().createFrameBufferScreenSized(0, 1);
        this.shader = LibAPIManager.instance().getGLFW().getRenderAPI().createShader();
    }
    
}
