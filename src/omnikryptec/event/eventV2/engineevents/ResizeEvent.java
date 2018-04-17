package omnikryptec.event.eventV2.engineevents;

import omnikryptec.event.eventV2.Event;

public class ResizeEvent extends Event {

	private int neww, newh;

	public ResizeEvent(int neww, int newh) {
		this.neww = neww;
		this.newh = newh;
		this.asyncExecution = false;
		this.asyncSubmission = false;
	}

	public int getNewWidth() {
		return neww;
	}

	public int getNewHeight() {
		return newh;
	}

}
