package omnikryptec.event;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import omnikryptec.display.DisplayManager;

/**
 * 
 * @author pcfreak9000
 *
 */
public class EventSystem {

	private static Map<EventType, List<IEventHandler>> eventhandler = new HashMap<>();
	private static List<EventType> types = new ArrayList<>();
	private static List<EventType> deftypes = new ArrayList<>();
	
	private static ExecutorService threadpool = null;
	
	private static EventSystem instance;
	
	static{
		 Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	            try {
	            	if(threadpool!=null){
		            	threadpool.shutdown();
		            	threadpool.awaitTermination(1, TimeUnit.MINUTES);
	            	}
	            } catch (Exception ex) {
	            }
	        }));
	}
	
	private EventSystem() {
		Field[] fields = EventType.class.getFields();
		for(int i=0; i<fields.length; i++){
			try {
				if(fields[i].get(null) instanceof EventType){
					deftypes.add((EventType) fields[i].get(null));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static EventSystem instance(){
		if(instance!=null){
			return instance;
		}
		if(DisplayManager.instance()==null){
			throw new NullPointerException("DisplayManager is null");
		}
		if(DisplayManager.instance().getSettings().getEventThreadpoolSize()>0){
			threadpool = Executors.newFixedThreadPool(DisplayManager.instance().getSettings().getEventThreadpoolSize());
		}else{
			threadpool = null;
		}
		instance = new EventSystem();
		return instance;
	}

	/**
	 * registers an eventtye
	 * 
	 * @param type
	 *            the eventtype
	 */
	public synchronized void addEventType(EventType type) {
		if (!types.contains(type)) {
			types.add(type);
		}
	}

	/**
	 * removes an eventtype form the allowed eventtypes
	 * 
	 * @param type
	 *            the eventtype
	 */
	public synchronized void removeType(EventType type) {
		types.remove(type);
	}

	/**
	 * registers eventhandler
	 * 
	 * @param handler
	 *            the eventhandler
	 * @param type
	 *            the eventtypes that affect the eventhandler
	 */
	public synchronized void addEventHandler(IEventHandler handler, EventType... type) {
		if (type == null || type.length <= 0) {
			throw new IllegalArgumentException("The EventType should be set!");
		}
		for (int i = 0; i < type.length; i++) {
			if (!types.contains(type[i]) && !deftypes.contains(type[i])) {
				throw new IllegalArgumentException("EventType \"" + type[i] + "\" is not registered!");
			}
			if (eventhandler.get(type[i]) == null) {
				eventhandler.put(type[i], new ArrayList<>());
			}
			eventhandler.get(type[i]).add(handler);
		}
	}

	/**
	 * fires an event for eventtypes
	 * 
	 * @param ev
	 *            the event
	 * @param type
	 *            the eventtypes
	 */
	public void fireEvent(Event ev, EventType... type) {
		if ((type == null || type.length <= 0)) {
			throw new IllegalArgumentException("The EventType should be set!");
		}
		for (int j = 0; j < type.length; j++) {
			if (!types.contains(type[j]) && !deftypes.contains(type[j])) {
				throw new IllegalArgumentException("EventType \"" + type[j] + "\" is not registered!");
			}
			if(type[j].executeInCurrentThread()||threadpool==null){
				event(ev, type[j]);
			}else{
				final int jtmp = j;
				threadpool.submit(()->{
					event(ev, type[jtmp]);
				});
			}
		}
	}

	private void event(Event ev, EventType type){
		if (eventhandler.get(type) != null) {
			ev.setEventType(type);
			for (int i = 0; i < eventhandler.get(type).size(); i++) {
				eventhandler.get(type).get(i).onEvent(new Event(ev));
			}
		}
	}
	
	/**
	 * removes an eventhandler from one eventtype
	 * 
	 * @param handler
	 *            the eventhandler to remove
	 * @param type
	 *            the eventtype
	 */
	public void removeEventHandlerFrom(IEventHandler handler, EventType type) {
		if (eventhandler.get(type) != null) {
			eventhandler.get(type).remove(handler);
			if (eventhandler.get(type).size() <= 0) {
				eventhandler.remove(type);
			}
		}
	}

	/**
	 * removes the eventhandler from all eventtypes
	 * 
	 * @param handler
	 *            the eventhandler to remove
	 */
	public void removeEventHandler(IEventHandler handler) {
		for (EventType t : eventhandler.keySet()) {
			removeEventHandlerFrom(handler, t);
		}
	}

	/**
	 * cleans the eventsystem up
	 */
	public static void cleanUp() {
		eventhandler.clear();
		types.clear();
	}

}
