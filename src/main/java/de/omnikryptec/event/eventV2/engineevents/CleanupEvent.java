package de.omnikryptec.event.eventV2.engineevents;

import de.omnikryptec.event.eventV2.Event;
import de.omnikryptec.main.OmniKryptecEngine;

public class CleanupEvent extends Event {

	public CleanupEvent() {
		super(OmniKryptecEngine.instance().ENGINE_BUS);
	}

}
