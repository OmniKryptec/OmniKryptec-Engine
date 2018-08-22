package omnikryptec.event.eventV2.engineevents;

import omnikryptec.event.eventV2.Event;
import omnikryptec.main.OmniKryptecEngine;

public class CleanupEvent extends Event {

	public CleanupEvent() {
		super(OmniKryptecEngine.instance().ENGINE_BUS);
	}

}
