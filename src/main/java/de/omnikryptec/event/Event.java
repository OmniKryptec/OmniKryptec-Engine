package de.omnikryptec.event;

public class Event {
    
    // event type specific variables
    protected boolean triggerSuperEventListeners = true;
    protected boolean consumeable = false;
    
    // Instance variables
    private boolean consumed = false;
    
    public boolean isConsumeable() {
        return consumeable;
    }
    
    public boolean triggersSuperEventListeners() {
        return triggerSuperEventListeners;
    }
    
    public void consume() {
        if (consumeable) {
            consumed = true;
        } else {
            throw new IllegalStateException("This event is not consumeable!");
        }
    }
    
    public boolean isConsumed() {
        return consumed;
    }
}
