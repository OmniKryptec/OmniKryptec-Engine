package omnikryptec.event.event;

/**
 * implement this class to listen for events of a special type. register it in
 * the EventSystem
 * 
 * @author pcfreak9000
 *
 */
@Deprecated
public interface IEventHandler {

	/**
	 * called the an event with a specified eventtype is fired the eventtype
	 * this reacts to can be set then registering this in the EventSystem.
	 * 
	 * @param ev
	 *            the event
	 */
	public void onEvent(Event ev);

}
