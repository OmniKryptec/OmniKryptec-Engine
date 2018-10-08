package de.omnikryptec.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class EventBus {

	private Multimap<Class<? extends Event>, IEventListener> listeners;

	private Queue<Event> eventQueue;

	private final AtomicBoolean processing = new AtomicBoolean(false);

	public EventBus() {
		this.eventQueue = new ConcurrentLinkedQueue<>();
		this.listeners = ArrayListMultimap.create();
	}

	public void register(IEventListener listener, Class<? extends Event> eventtype) {
		listeners.put(eventtype, listener);
	}

	public void register(Object object) {
		Method[] methods;
		if (object instanceof Class<?>) {
			methods = ((Class<?>) object).getDeclaredMethods();
			object = null;
		} else {
			methods = object.getClass().getDeclaredMethods();
		}
		boolean annotationFound = false;
		for (Method m : methods) {
			if (object == null && !Modifier.isStatic(m.getModifiers())) {
				throw new IllegalArgumentException("Nonstatic event listener registered by class");
			}
			if (m.isAnnotationPresent(EventSubscription.class)) {
				annotationFound = true;
				EventHandler handler = new EventHandler(object, m);
				Class<?>[] cls = m.getParameterTypes();
				if (cls.length != 1) {
					throw new IllegalArgumentException("Wrong amount of parameter types in event listener: " + object);
				} else {
					if (Event.class.isAssignableFrom(cls[0])) {
						if (listeners.containsKey(cls[0]) && listeners.containsEntry(cls[0], handler)) {
							continue;
						}
						register(handler, (Class<? extends Event>) cls[0]);
					} else {
						throw new IllegalArgumentException("Wrong type of parameter in event listener: " + object);
					}
				}
			}
		}
		if (!annotationFound) {
			throw new IllegalArgumentException("No EventSubscriptions found: " + object);
		}
	}

	public void enqueueOrPost(Event event, boolean postImmediately) {
		if (postImmediately) {
			processEvent(event);
		} else {
			eventQueue.add(event);
		}
	}

	public void processQueuedEvents() {
		if (processing.get()) {
			throw new IllegalStateException("Already processing!");
		}
		processing.set(true);
		while (!eventQueue.isEmpty()) {
			processEvent(eventQueue.poll());
		}
		processing.set(false);
	}

	public boolean isProcessing() {
		return processing.get();
	}

	private void processEvent(Event event) {
		Class<?> someclazz = event.getClass();
		do {
			for (IEventListener listener : listeners.get((Class<? extends Event>) someclazz)) {
				if (!event.isConsumeable() || !event.isConsumed()) {
					listener.invoke(event);
				}
			}
			someclazz = someclazz.getSuperclass();
		} while (event.triggersSuperEventListeners() && someclazz != Object.class && someclazz != null);
	}
}
