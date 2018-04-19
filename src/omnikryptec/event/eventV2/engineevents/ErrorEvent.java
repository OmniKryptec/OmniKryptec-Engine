package omnikryptec.event.eventV2.engineevents;

import omnikryptec.event.eventV2.Event;
import omnikryptec.event.eventV2.EventBus;
import omnikryptec.main.OmniKryptecEngine;

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
