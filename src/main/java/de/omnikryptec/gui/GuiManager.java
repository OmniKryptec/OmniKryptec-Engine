package de.omnikryptec.gui;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.input.InputEvent;
import de.omnikryptec.render.renderer.LocalRendererContext;

public class GuiManager {
    
    private LocalRendererContext rendererContext;
    private GuiRenderer renderer;
    
    private GuiComponent componentRoot;
    
    public GuiManager(LocalRendererContext renderer) {
        this.rendererContext = renderer;
        LibAPIManager.ENGINE_EVENTBUS.register(this);
        setRenderer(new GuiRenderer());
    }
    
    public void setRenderer(GuiRenderer renderer) {
        if (this.renderer != null) {
            rendererContext.removeRenderer(renderer);
            this.renderer.setGui(null);
        }
        this.renderer = renderer;
        if (this.renderer != null) {
            rendererContext.addRenderer(renderer);
            if (componentRoot != null) {
                this.renderer.setGui(componentRoot);
            }
        }
    }
    
    public void setGui(GuiComponent componentRoot) {
        this.componentRoot = componentRoot;
        if (this.renderer != null) {
            this.renderer.setGui(componentRoot);
        }
    }
    
    @EventSubscription(priority = 2000, receiveConsumed = false)
    public void event(InputEvent event) {
        if (this.componentRoot != null) {
            this.componentRoot.getEventBus().post(event);
        }
    }
}
