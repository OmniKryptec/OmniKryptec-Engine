package de.omnikryptec.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class EventBus {

	private Multimap<Class<? extends Event>, IEventListener> listeners;
	
	private Queue<Event> eventQueue;
	
	private final AtomicBoolean processing = new AtomicBoolean(false);
	
	public EventBus() {
		this.eventQueue = new ConcurrentLinkedQueue<>();
		this.listeners = ArrayListMultimap.create();
	}
	
	public void enqueueOrPost(Event event, boolean postImmediately) {
		if(postImmediately) {
			processEvent(event);
		}else {
			eventQueue.add(event);
		}
	}
	
	public void processQueuedEvents() {
		if(processing.get()) {
			throw new IllegalStateException("Already processing!");
		}
		processing.set(true);
		while(!eventQueue.isEmpty()) {
			processEvent(eventQueue.poll());
		}
		processing.set(false);
	}
	
	public boolean isProcessing() {
		return processing.get();
	}
	
	private void processEvent(Event event) {
		for(IEventListener listener : listeners.get(event.getClass())) {
			listener.invoke(event);
		}
	}
}
