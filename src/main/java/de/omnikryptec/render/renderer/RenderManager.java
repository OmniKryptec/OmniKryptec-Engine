package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.RenderState.BlendMode;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.opengl.framebuffer.GLScreenBuffer.ScreenBufferResizedEvent;
import de.omnikryptec.util.updater.Time;

//TODO pcfreak9000 if there is only one ViewManager in the list, render it directly
public class RenderManager {

    private static final Comparator<ViewManagerInfo> VMI_COMP = (v1, v2) -> v1.priority - v2.priority;
    private static final RenderState DEFAULT_SCREENWRITER_STATE = RenderState.of(BlendMode.ALPHA);

    private static RenderManager instance;

    private static class ViewManagerInfo {
        FrameBuffer targetFbo;
        int priority;
        ViewManager viewManager;
    }

    private final List<ViewManagerInfo> views;

    public RenderManager() {
        if (instance != null) {
            throw new IllegalStateException("RenderManager already exists");
        }
        this.views = new ArrayList<>();
        LibAPIManager.ENGINE_EVENTBUS.register(this);
        instance = this;
    }

    public ViewManager createAndAddViewManager(int prio) {
        ViewManager vm = new ViewManager();
        addViewManager(vm, prio);
        return vm;
    }

    public void addViewManager(ViewManager viewMgr, int prio) {
        ViewManagerInfo inf = new ViewManagerInfo();
        inf.viewManager = viewMgr;
        inf.priority = prio;
        inf.targetFbo = LibAPIManager.instance().getGLFW().getRenderAPI().createFrameBufferScreenSized(0, 1);
        inf.targetFbo.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        viewMgr.getMainView().setTargetFbo(inf.targetFbo);//TODx pcfreak9000 Hmmmmmm... is there a better way?
        this.views.add(inf);
        this.views.sort(VMI_COMP);
    }

    public void removeViewManager(ViewManager viewMgr) {
        Iterator<ViewManagerInfo> it = this.views.iterator();
        while (it.hasNext()) {
            ViewManagerInfo vmI = it.next();
            if (vmI.viewManager.equals(viewMgr)) {
                vmI.targetFbo.deleteAndUnregister();
                it.remove();
            }
        }
    }

    @EventSubscription
    public void event(final ScreenBufferResizedEvent ev) {
        for (ViewManagerInfo vmInf : this.views) {
            vmInf.targetFbo = vmInf.targetFbo.resizedCloneAndDelete(ev.width, ev.height);
            vmInf.viewManager.getMainView().setTargetFbo(vmInf.targetFbo);//TODO pcfreak9000 this is oof, also see the above TODx
        }
    }

    public void renderAll(Time time) {
        Texture[] viewTextures = new Texture[this.views.size()];
        for (int i = 0; i < this.views.size(); i++) {
            ViewManagerInfo vmInf = this.views.get(i);
            vmInf.viewManager.renderInstance(time);
            viewTextures[i] = vmInf.targetFbo.getTexture(0);
        }
        LibAPIManager.instance().getGLFW().getRenderAPI().getCurrentFrameBuffer().clearComplete();
        LibAPIManager.instance().getGLFW().getRenderAPI().applyRenderState(DEFAULT_SCREENWRITER_STATE);
        RendererUtil.renderDirect(viewTextures);
    }

}
