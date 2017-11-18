package omnikryptec.event.event;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import omnikryptec.display.DisplayManager;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.settings.GameSettings;

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

    static {
        OmniKryptecEngine.addShutdownHook(() -> {
            try {
                if (threadpool != null) {
                    threadpool.shutdownNow();
                    threadpool = null;
                }
            } catch (Exception ex) {
            }
        });
    }

    public EventSystem() {
    	if(instance!=null) {
    		return;
    	}
    	Field[] fields = EventType.class.getFields();
        for (Field field : fields) {
            try {
                if (field.get(null) instanceof EventType) {
                    deftypes.add((EventType) field.get(null));
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
	    if (OmniKryptecEngine.instance().getDisplayManager().getSettings().getInteger(GameSettings.THREADPOOLSIZE_EVENT) > 0) {
	        threadpool = Executors.newFixedThreadPool(OmniKryptecEngine.instance().getDisplayManager().getSettings().getInteger(GameSettings.THREADPOOLSIZE_EVENT));
	    } else {
	        threadpool = null;
	    }
    	instance=this;
    }

    /**
     * registers an eventtye
     *
     * @param type the eventtype
     */
    public synchronized void addEventType(EventType type) {
        if (!types.contains(type)) {
            types.add(type);
        }
    }

    /**
     * removes an eventtype form the allowed eventtypes
     *
     * @param type the eventtype
     */
    public synchronized void removeType(EventType type) {
        types.remove(type);
    }

    /**
     * registers eventhandler
     *
     * @param handler the eventhandler
     * @param type the eventtypes that affect the eventhandler
     */
    public synchronized void addEventHandler(IEventHandler handler, EventType... type) {
        if (type == null || type.length <= 0) {
            throw new IllegalArgumentException("The EventType should be set!");
        }
        for (EventType type1 : type) {
            if (!types.contains(type1) && !deftypes.contains(type1)) {
                throw new IllegalArgumentException("EventType \"" + type1 + "\" is not registered!");
            }
            if (eventhandler.get(type1) == null) {
                eventhandler.put(type1, new ArrayList<>());
            }
            eventhandler.get(type1).add(handler);
        }
    }

    /**
     * fires an event for eventtypes
     *
     * @param ev the event
     * @param type the eventtypes
     */
    public void fireEvent(Event ev, EventType... type) {
        if ((type == null || type.length <= 0)) {
            throw new IllegalArgumentException("The EventType should be set!");
        }
        for (EventType type1 : type) {
            if (!types.contains(type1) && !deftypes.contains(type1)) {
                throw new IllegalArgumentException("EventType \"" + type1 + "\" is not registered!");
            }
            if (type1.executeInCurrentThread() || threadpool == null) {
                event(ev, type1);
            } else {
                final EventType type1tmp = type1;
                threadpool.submit(() -> {
                    event(ev, type1tmp);
                });
            }
        }
    }

    private void event(Event ev, EventType type) {
        List<IEventHandler> handlers = eventhandler.get(type);
        if (handlers != null) {
            ev.setEventType(type);
            handlers.stream().forEach((handler) -> {
                handler.onEvent(new Event(ev));
            });
        }
    }

    /**
     * removes an eventhandler from one eventtype
     *
     * @param handler the eventhandler to remove
     * @param type the eventtype
     */
    public void removeEventHandlerFrom(IEventHandler handler, EventType type) {
        List<IEventHandler> handlers = eventhandler.get(type);
        if (handlers != null) {
            handlers.remove(handler);
            if (handlers.isEmpty()) {
                eventhandler.remove(type);
            }
        }
    }

    /**
     * removes the eventhandler from all eventtypes
     *
     * @param handler the eventhandler to remove
     */
    public void removeEventHandler(IEventHandler handler) {
        eventhandler.keySet().stream().forEach((t) -> {
            removeEventHandlerFrom(handler, t);
        });
    }

    /**
     * cleans the eventsystem up
     */
    public static void cleanUp() {
        eventhandler.clear();
        types.clear();
        instance = null;
    }

}
