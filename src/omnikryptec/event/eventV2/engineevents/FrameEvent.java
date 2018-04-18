package omnikryptec.event.eventV2.engineevents;

import omnikryptec.event.eventV2.Event;

public class FrameEvent extends Event {

	public static enum FrameType {
		PRE, POST, STARTSCENE, ENDSCENE
	}

	private FrameType tp;

	public FrameEvent(FrameType tp) {
		this.tp = tp;
	}

	public FrameType getType() {
		return tp;
	}

}
