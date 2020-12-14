package de.omnikryptec.render3.postprocessing;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.shader.ShaderProgram;

public abstract class AbstractPostProcessor implements Postprocessor {
    
    protected FrameBuffer buffer;
    protected ShaderProgram shaderProgram;
    
    public AbstractPostProcessor() {
        this.buffer = LibAPIManager.instance().getGLFW().getRenderAPI().createFrameBufferScreenSized(0, 1);
        this.shaderProgram = LibAPIManager.instance().getGLFW().getRenderAPI().createShader();
    }
    
}
