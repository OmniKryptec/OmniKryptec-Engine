package de.omnikryptec.event.eventV3;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventBus {

	private Queue<Event> eventQueue;
	
	private final AtomicBoolean processing = new AtomicBoolean(false);
	
	public EventBus() {
		this.eventQueue = new ConcurrentLinkedQueue<>();
	}
	
	public void enqueueOrPost(Event e) {
		if(e.isPostDirect()) {
			processEvent(e);
		}else {
			eventQueue.add(e);
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
	
	private void processEvent(Event e) {
		//dispatch event
	}
}
