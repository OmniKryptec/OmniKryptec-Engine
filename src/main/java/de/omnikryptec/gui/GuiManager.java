package de.omnikryptec.gui;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.input.InputEvent;
import de.omnikryptec.render.renderer.LocalRendererContext;

public class GuiManager {
    
    private final LocalRendererContext rendererContext;
    private GuiRenderer renderer;
    
    private GuiComponent componentRoot;
    
    public GuiManager(final LocalRendererContext renderer) {
        this.rendererContext = renderer;
        LibAPIManager.ENGINE_EVENTBUS.register(this);
        setRenderer(new GuiRenderer());
    }
    
    public void setRenderer(final GuiRenderer renderer) {
        if (this.renderer != null) {
            this.rendererContext.removeRenderer(renderer);
            this.renderer.setGui(null);
        }
        this.renderer = renderer;
        if (this.renderer != null) {
            this.rendererContext.addRenderer(renderer);
            if (this.componentRoot != null) {
                this.renderer.setGui(this.componentRoot);
            }
        }
    }
    
    public void setGui(final GuiComponent componentRoot) {
        this.componentRoot = componentRoot;
        if (this.renderer != null) {
            this.renderer.setGui(componentRoot);
        }
        recalculateConstraints();
    }
    
    private void recalculateConstraints() {
        if (this.componentRoot != null) {
            this.componentRoot.setConstraints(new GuiConstraints(0, 0, 1, 1));
        }
    }
    
    //    @EventSubscription
    //    public void windowEvent(WindowEvent.ScreenBufferResized ev) {
    //        recalculateConstraints();
    //    }
    
    @EventSubscription(priority = 2000, receiveConsumed = false)
    public void event(final InputEvent event) {
        if (this.componentRoot != null) {
            this.componentRoot.getEventBus().post(event);
        }
    }
}
