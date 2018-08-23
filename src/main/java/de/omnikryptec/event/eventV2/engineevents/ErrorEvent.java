package de.omnikryptec.event.eventV2.engineevents;

import de.omnikryptec.event.eventV2.Event;
import de.omnikryptec.main.OmniKryptecEngine;

public class ErrorEvent extends Event {

	private Exception myexcp;
	private Object source;

	public ErrorEvent(Exception e, Object source) {
		super(OmniKryptecEngine.instance().ENGINE_BUS);
		this.myexcp = e;
		this.source = source;
		this.consumeable = false;
	}

	public Exception getError() {
		return myexcp;
	}

	public Object getSource() {
		return source;
	}

}
