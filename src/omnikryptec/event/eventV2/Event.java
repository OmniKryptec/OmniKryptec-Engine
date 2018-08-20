package omnikryptec.event.eventV2;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Event {

	private boolean called = false;
	private final AtomicBoolean consumed = new AtomicBoolean(false);
	protected boolean asyncSubmission = false;
	protected boolean asyncExecution = false;
	protected boolean consumeable = true;
	private EventBus bus;
	
	protected Event(EventBus poster) {
		this.bus = poster;
	}
	
	public void call() {
		if (called == true) {
			return;
		}
		called = true;
		bus.submit(this);
	}

	public Event consume() {
		if(isConsumeable()) {
			consumed.set(true);
		}
		return this;
	}

	public EventBus getBus() {
		return bus;
	}
	
	public boolean isConsumed() {
		return consumed.get();
	}

	public boolean isCalled() {
		return called;
	}

	public Event setAsyncSubmission(boolean b) {
		this.asyncSubmission = b;
		return this;
	}

	public Event setAsyncExecution(boolean b) {
		this.asyncExecution = b;
		return this;
	}

	public boolean isAsyncSubmission() {
		return asyncSubmission;
	}

	public boolean isAsyncExecution() {
		return asyncExecution;
	}

	public Event setConsumeable(boolean b) {
		this.consumeable = b;
		return this;
	}

	public boolean isConsumeable() {
		return consumeable;
	}
	
	public void beforeExecution(EventHandler handler) {
	}

}
