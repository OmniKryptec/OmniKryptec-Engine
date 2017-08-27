package omnikryptec.event.event;

import omnikryptec.main.AbstractScene;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;

/**
 * 
 * @author pcfreak9000
 *
 */
public class Event {

	private EventType type;
	private AbstractScene scene;
	private Object[] msg;

	/**
	 * creates an event without a message (= message is null)
	 */
	public Event() {
		this((Object) null);
	}

	/**
	 * creates an event with a message
	 * 
	 * @param msg
	 *            the message
	 */
	public Event(Object... msg) {
		this.msg = msg;
	}

	/**
	 * used by the engine
	 * 
	 * @param ev
	 */
	protected Event(Event ev) {
		type = ev.getType();
		scene = ev.getScene() == null ? OmniKryptecEngine.instance().getCurrentScene() : ev.getScene();
		msg = ev.getMsgA();
	}

	protected void setEventType(EventType type) {
		this.type = type;
	}

	/**
	 * the eventtype. from the class EventType or one of your own events
	 * 
	 * @return the eventytpe
	 */
	public EventType getType() {
		return type;
	}

	/**
	 * the scene when the event is fired
	 * 
	 * @return a scene
	 */
	public AbstractScene getScene() {
		return scene;
	}

	/**
	 * the messages of this event or null if no message is set
	 * 
	 * @return a message
	 */
	public Object[] getMsgA() {
		return msg;
	}

	public Object getMsg() {
		if (msg != null && msg.length > 0) {
			return msg[0];
		} else {
			return null;
		}
	}

}
