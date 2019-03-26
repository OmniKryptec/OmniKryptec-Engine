package de.omnikryptec.render;

import de.omnikryptec.core.update.ILayer;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.render.postprocessing.Postprocessor;
import de.omnikryptec.util.updater.Time;

public class MasterRenderer implements IUpdatable {
    
    private RendererContext context;
    private Postprocessor postprocessor;
    
    private FrameBuffer scene;
    private FrameBuffer sceneMultisampled;
    
    public MasterRenderer(RendererContext context, int multisamples, FBTarget... targets) {
        this.context = context;
    }
    
    @Override
    public void update(Time time) {
        this.sceneMultisampled.bindFrameBuffer();
        //TODO clear
        this.context.update(time);
        this.sceneMultisampled.unbindFrameBuffer();
        //TODO sceneMultisampled -> scene 
        if (this.postprocessor != null) {
            this.postprocessor.postprocess(time, scene);
        }
    }
    
    @Override
    public void init(ILayer layer) {
        this.context.init(layer);
    }
    
    @Override
    public void deinit(ILayer layer) {
        this.context.deinit(layer);
    }
    
    private void createAndResizeFBOs(int multisample, FBTarget... targets) {
        if (sceneMultisampled == null) {
            SurfaceBuffer surface = context.getRenderAPI().getWindow().getDefaultFrameBuffer();
            sceneMultisampled = context.getRenderAPI().createFrameBuffer(surface.getWidth(), surface.getHeight(),
                    multisample, targets.length);
            sceneMultisampled.bindFrameBuffer();
            sceneMultisampled.assignTargets(targets);
            sceneMultisampled.unbindFrameBuffer();
        }else {
            
        }
    }
}
