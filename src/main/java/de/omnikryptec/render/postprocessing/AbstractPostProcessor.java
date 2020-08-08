package de.omnikryptec.render.postprocessing;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.render.renderer.View;
import de.omnikryptec.util.updater.Time;

public abstract class AbstractPostProcessor implements Postprocessor{
    
    protected FrameBuffer buffer;
    protected Shader shader;
    
    public AbstractPostProcessor() {
        this.buffer = LibAPIManager.instance().getGLFW().getRenderAPI().createFrameBufferScreenSized(0, 1);
        this.shader = LibAPIManager.instance().getGLFW().getRenderAPI().createShader();
    }
    
}
