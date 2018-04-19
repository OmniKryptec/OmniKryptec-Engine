package omnikryptec.event.eventV2;

public abstract class Event {

	private boolean called = false;
	private boolean consumed = false;
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
			consumed = true;
		}
		return this;
	}

	public EventBus getBus() {
		return bus;
	}
	
	public boolean isConsumed() {
		return consumed;
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
		return !asyncExecution && consumeable;
	}

}
