package omnikryptec.event.eventV2.engineevents;

import omnikryptec.event.eventV2.Event;
import omnikryptec.main.OmniKryptecEngine;

public class ResizeEvent extends Event {

	private int neww, newh;

	public ResizeEvent(int neww, int newh) {
		super(OmniKryptecEngine.instance().ENGINE_BUS);
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
