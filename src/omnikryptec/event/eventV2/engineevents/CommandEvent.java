package omnikryptec.event.eventV2.engineevents;

import omnikryptec.event.eventV2.Event;
import omnikryptec.util.logger.Command;

public class CommandEvent extends Event {

	private Command c;
	private String args;

	public CommandEvent(Command c, String args) {
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
