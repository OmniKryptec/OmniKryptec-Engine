package de.omnikryptec.gui;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.Util;

public class GuiComponent {
    
    //Until better constraints are set so layouts etc work when creating the GUI, maybe only run layout stuff when needed like before rendering?
    private static final GuiConstraints DEFAULT_CONSTRAINTS = new GuiConstraints(0, 0, 1, 1);
    
    private GuiLayout layout;
    
    private GuiConstraints constraints;
    
    private List<GuiComponent> children;
    private EventBus events;
    
    public GuiComponent() {
        this.layout = null;
        this.constraints = DEFAULT_CONSTRAINTS;
        this.children = new ArrayList<>();
        this.events = new EventBus();
        this.events.setPriority(2000);
        events.setReceiveConsumed(false);
        events.register(this);
    }
    
    public void addComponent(GuiComponent comp) {
        children.add(comp);
        this.events.register(comp.events);
        revalidateLayout();
    }
    
    public void removeComponent(GuiComponent comp) {
        children.remove(comp);
        this.events.unregister(comp.events);
        revalidateLayout();
    }
    
    public void setLayout(GuiLayout layout) {
        this.layout = layout;
        revalidateLayout();
    }
    
    public void revalidateLayout() {
        if (this.layout != null) {
            layout.doLayout(this, children);
        }else {
            for(GuiComponent gc : children) {
                gc.setConstraints(this.constraints);
            }
        }
    }
    
    public void setConstraints(GuiConstraints constraints) {
        this.constraints = Util.ensureNonNull(constraints);
        revalidateLayout();
        calculateActualPosition(this.constraints);
    }
    
    public GuiConstraints getConstraints() {
        return constraints;
    }
    
    public void render(Batch2D batch) {
        renderComponent(batch);
        for (GuiComponent gc : children) {
            gc.render(batch);
        }
    }
    
    EventBus getEventBus() {
        return events;
    }
    
    protected void renderComponent(Batch2D batch) {
        
    }
    
    protected void calculateActualPosition(GuiConstraints constraints) {
        //use the current constraints and some other stuff to calculate the components actual pos, width and height here
    }
    
}
