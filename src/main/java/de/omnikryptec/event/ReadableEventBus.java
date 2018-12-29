package de.omnikryptec.event;

public interface ReadableEventBus {
    
    public void register(final IEventListener listener, final Class<? extends Event> eventtype);
    
    public void register(Object object);

    public void post(final Event event);
    
    public void enqueue(final Event event);

}
