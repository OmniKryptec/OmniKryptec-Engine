package omnikryptec.event.eventV2;

public abstract class Event {

	private boolean usesCurrentThread = false;
	private boolean called = false;

	public void call() {
		if (called == true) {
			return;
		}
		called = true;
		EventSystem.submit(this);
	}

	public Event setUseCurrentThread(boolean b) {
		this.usesCurrentThread = b;
		return this;
	}

	public boolean usesCurrentThread() {
		return usesCurrentThread;
	}

}
