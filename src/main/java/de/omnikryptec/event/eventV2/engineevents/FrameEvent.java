package de.omnikryptec.event.eventV2.engineevents;

import de.omnikryptec.event.eventV2.Event;
import de.omnikryptec.main.OmniKryptecEngine;

public class FrameEvent extends Event {

	public static enum FrameType {
		PRE, POST, STARTSCENE, ENDSCENE
	}

	private FrameType tp;

	public FrameEvent(FrameType tp) {
		super(OmniKryptecEngine.instance().ENGINE_BUS);
		this.tp = tp;
	}

	public FrameType getType() {
		return tp;
	}

}
