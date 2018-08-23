/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.event.eventV2;

import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class EventBus {

	private static Comparator<EventHandler> comp = new Comparator<EventHandler>() {

		@Override
		public int compare(EventHandler o1, EventHandler o2) {
			return (int) Math.signum(o1.getMethod().getAnnotation(EventSubscription.class).priority()
					- o2.getMethod().getAnnotation(EventSubscription.class).priority());
		}

	};
	private static List<EventBus> eventbusses = new ArrayList<>();

	private HashMap<Class<? extends Event>, List<EventHandler>> eventhandlers = new HashMap<>();
	private ExecutorService execpool;
	private ExecutorService submitterpool;
	private String name;

	static {
		OmniKryptecEngine.addShutdownHook(() -> {
			try {
				clean();
			} catch (Exception ex) {
			}
		});
	}

	public static void clean() {
		for (EventBus b : eventbusses) {
			b.cleanInstance();
		}
		eventbusses.clear();
	}

	public static EventBus forName(String s) {
		for (EventBus eb : eventbusses) {
			if (eb.name.equals(s)) {
				return eb;
			}
		}
		return null;
	}

	public EventBus(String name, int exec, int subm) {
		eventbusses.add(this);
		execpool = Executors.newFixedThreadPool(exec);
		submitterpool = Executors.newFixedThreadPool(subm);
		this.name = name;
	}

	public void submit(Event event) {
		if (event.isAsyncSubmission() && submitterpool != null) {
			submitterpool.submit(() -> __submit(event));
		} else {
			__submit(event);
		}
	}

	private void __submit(Event event) {
		List<EventHandler> list = eventhandlers.get(event.getClass());
		if (list == null) {
			return;
		}
		for (final EventHandler m : list) {
			m.getMethod().setAccessible(true);
			Runnable handlerInvocation = new Runnable() {

				@Override
				public void run() {
					if(event.isConsumed()) {
						return;
					}
					try {
						event.beforeExecution(m);
						m.getMethod().invoke(m.getHandler(), event);
					} catch (IllegalAccessException e) {
						Logger.log("Could not call eventhandler: " + e, LogLevel.ERROR);
					} catch (IllegalArgumentException e) {
						Logger.logErr("Some IllegalArgumentException: ", e);
					} catch (InvocationTargetException e) {
						Logger.log("Event failed: " + e, LogLevel.ERROR);
						Logger.logErr("Stacktrace: ", e);
					}
				}
			};
			if (event.isAsyncExecution() && execpool != null) {
				execpool.submit(handlerInvocation);
			} else {
				handlerInvocation.run();
			}
			if (event.isConsumeable() && event.isConsumed()) {
				break;
			}
		}
	}

	public void findStaticEventAnnotations(ClassLoader loader, Predicate<Class<?>> filter) {
		Vector<Class<?>> it = list(loader);
		for (Class<?> clazz : it) {
			if (filter == null || filter.test(clazz)) {
				registerEventHandler(clazz);
			}
		}
	}

	
	public void registerEventHandler(Object o) {
		if (o == null) {
			return;
		}
		Method[] methods;
		if (o instanceof Class<?>) {
			methods = ((Class<?>) o).getDeclaredMethods();
			o = null;
		} else {
			methods = o.getClass().getDeclaredMethods();
		}
		boolean foundsmth = false;
		for (Method m : methods) {
			if (o == null && !Modifier.isStatic(m.getModifiers())) {
				continue;
			}
			if (m.isAnnotationPresent(EventSubscription.class)) {
				String wantedName = m.getAnnotation(EventSubscription.class).eventBusName();
				if (!wantedName.equals("") && !wantedName.equals(name)) {
					continue;
				}
				EventHandler handler = new EventHandler(o, m);
				Class<?>[] cls = m.getParameterTypes();
				if (cls.length != 1) {
					Logger.log("The eventhandler " + (o == null ? o : o.getClass())
							+ " uses incorrect event-handling methods!", LogLevel.WARNING);
				} else {
					if (Event.class.isAssignableFrom(cls[0])) {
						if (eventhandlers.containsKey(cls[0]) && eventhandlers.get(cls[0]).contains(handler)) {
							continue;
						}
						put((Class<? extends Event>) cls[0], handler);
						foundsmth = true;
					} else {
						Logger.log(
								"Event-handling method is useless, parameter is not an event in "
										+ (o == null ? o : o.getClass()) + ", method " + m.toString(),
								LogLevel.WARNING);
					}
				}
			}
		}
		if (foundsmth) {
			for (List<EventHandler> l : eventhandlers.values()) {
				l.sort(comp);
			}
		}
	}

	private void put(Class<? extends Event> o, EventHandler m) {
		if (eventhandlers.get(o) == null) {
			eventhandlers.put(o, new ArrayList<>());
		}
		eventhandlers.get(o).add(m);
	}

	
	private static Vector<Class<?>> list(ClassLoader CL) {
		try {
			Class<?> CL_class = CL.getClass();
			while (CL_class != ClassLoader.class) {
				CL_class = CL_class.getSuperclass();
			}
			Field ClassLoader_classes_field = CL_class.getDeclaredField("classes");
			ClassLoader_classes_field.setAccessible(true);
			Vector<Class<?>> classes = (Vector<Class<?>>) ClassLoader_classes_field.get(CL);
			return (Vector<Class<?>>) classes.clone();
		} catch (Exception e) {
			return null;
		}
	}

	public void cleanInstance() {
		eventhandlers = null;
		if (execpool != null) {
			execpool.shutdownNow();
			execpool = null;
		}
		if (submitterpool != null) {
			submitterpool.shutdownNow();
			submitterpool = null;
		}
	}

	public String getName() {
		return name;
	}

}
