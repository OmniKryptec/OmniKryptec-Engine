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
    
    private final List<GuiComponent> children;
    private final EventBus events;
    
    public GuiComponent() {
        this.layout = null;
        this.constraints = DEFAULT_CONSTRAINTS;
        this.children = new ArrayList<>();
        this.events = new EventBus(2000);
        this.events.setReceiveConsumed(false);
        this.events.register(this);
    }
    
    public void addComponent(final GuiComponent comp) {
        this.children.add(comp);
        this.events.register(comp.events);
        revalidateLayout();
    }
    
    public void removeComponent(final GuiComponent comp) {
        this.children.remove(comp);
        this.events.unregister(comp.events);
        revalidateLayout();
    }
    
    public void setLayout(final GuiLayout layout) {
        this.layout = layout;
        revalidateLayout();
    }
    
    public void revalidateLayout() {
        if (this.layout != null) {
            this.layout.doLayout(this, this.children);
        } else {
            for (final GuiComponent gc : this.children) {
                gc.setConstraints(this.constraints);
            }
        }
    }
    
    public void setConstraints(final GuiConstraints constraints) {
        this.constraints = Util.ensureNonNull(constraints);
        revalidateLayout();
        calculateActualPosition(this.constraints);
    }
    
    public GuiConstraints getConstraints() {
        return this.constraints;
    }
    
    public void render(final Batch2D batch) {
        renderComponent(batch);
        for (final GuiComponent gc : this.children) {
            gc.render(batch);
        }
    }
    
    EventBus getEventBus() {
        return this.events;
    }
    
    protected void renderComponent(final Batch2D batch) {
        
    }
    
    protected void calculateActualPosition(final GuiConstraints constraints) {
        //use the current constraints and some other stuff to calculate the components actual pos, width and height here
    }
    
}
