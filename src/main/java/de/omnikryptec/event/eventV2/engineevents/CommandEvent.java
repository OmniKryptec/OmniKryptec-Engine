package de.omnikryptec.event.eventV2.engineevents;

import de.omnikryptec.event.eventV2.Event;
import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.util.logger.Command;

public class CommandEvent extends Event {

	private Command c;
	private String args;

	public CommandEvent(Command c, String args) {
		super(OmniKryptecEngine.instance().ENGINE_BUS);
		this.c = c;
		this.args = args;
	}

	public Command getCommand() {
		return c;
	}

	public String getArguments() {
		return args;
	}

}
