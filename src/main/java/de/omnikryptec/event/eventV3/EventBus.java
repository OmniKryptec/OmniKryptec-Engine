package de.omnikryptec.event.eventV3;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventBus {

	private Queue<Event> eventQueue;
	
	private boolean processing = false;
	
	public EventBus() {
		this.eventQueue = new ConcurrentLinkedQueue<>();
	}
	
	public void enqueue(Event e) {
		eventQueue.add(e);
	}
	
	public void processQueuedEvents() {
		if(isProcessing()) {
			throw new IllegalStateException("Already processing!");
		}
		processing = true;
		while(!eventQueue.isEmpty()) {
			processEvent(eventQueue.poll());
		}
		processing = false;
	}
	
	public boolean isProcessing() {
		return processing;
	}
	
	private void processEvent(Event e) {
		
	}
}
