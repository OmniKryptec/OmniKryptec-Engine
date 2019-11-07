package de.omnikryptec.gui;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.render.batch.Batch2D;

public class GuiComponent {
    
    private List<GuiComponent> children;    
    private EventBus events;
    
    public GuiComponent() {
        this.children = new ArrayList<>();
        this.events = new EventBus();
        this.events.setPriority(2000);
        events.setReceiveConsumed(false);
        events.register(this);
    }
    
    public void addComponent(GuiComponent comp) {
        children.add(comp);
        this.events.register(comp.events);
    }
    
    public void removeComponent(GuiComponent comp) {
        children.remove(comp);
        this.events.unregister(comp.events);
    }
    
    EventBus getEventBus() {
        return events;
    }
    
    protected void renderComponent(Batch2D batch) {
        
    }
    
    public void render(Batch2D batch) {
        renderComponent(batch);
        for (GuiComponent gc : children) {
            gc.render(batch);
        }
    }
    
}
