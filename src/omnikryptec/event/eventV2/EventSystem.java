package omnikryptec.event.eventV2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class EventSystem {

	private static Comparator<EventHandler> comp = new Comparator<EventHandler>() {

		@Override
		public int compare(EventHandler o1, EventHandler o2) {
			return (int) Math.signum(o1.getMethod().getAnnotation(EventSubscription.class).priority()
					- o2.getMethod().getAnnotation(EventSubscription.class).priority());
		}

	};

	private static HashMap<Class<? extends Event>, List<EventHandler>> eventhandlers = new HashMap<>();
	private static ExecutorService threadpool;
	private static ExecutorService submitterpool;
	private static boolean init = false;

	public static void init(int exec, int subm) {
		if (init) {
			return;
		}
		init = true;
		threadpool = Executors.newFixedThreadPool(exec);
		submitterpool = Executors.newFixedThreadPool(subm);
	}

	static {
		OmniKryptecEngine.addShutdownHook(() -> {
			try {
				clean();
			} catch (Exception ex) {
			}
		});
	}

	private EventSystem() {
	}

	public static void submit(Event event) {
		if (event.isAsyncSubmission()) {
			submitterpool.submit(() -> __submit(event));
		} else {
			__submit(event);
		}
	}

	private static void __submit(Event event) {
		List<EventHandler> list = eventhandlers.get(event.getClass());
		if (list == null) {
			return;
		}
		for (EventHandler m : list) {
			try {
				if (event.isAsyncExecution()) {
					threadpool.submit(() -> m.getMethod().invoke(m.getHandler(), event));
				} else {
					m.getMethod().invoke(m.getHandler(), event);
				}
			} catch (IllegalAccessException e) {
				Logger.log("Could not call eventhandler: " + e, LogLevel.ERROR);
			} catch (IllegalArgumentException e) {
				Logger.logErr("Some IllegalArgumentException: ", e);
			} catch (InvocationTargetException e) {
				Logger.log("Event failed: " + e, LogLevel.ERROR);
				Logger.logErr("", e);
			}
			if (event.isConsumeable() && event.isConsumed()) {
				break;
			}
		}
	}

	public static void findStaticEventAnnotations(ClassLoader loader, Predicate<Class<?>> filter) {
		Vector<Class<?>> it = list(loader);
		for (Class<?> clazz : it) {
			if (filter == null || filter.test(clazz)) {
				registerEventHandler(clazz);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void registerEventHandler(Object o) {
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

	private static void put(Class<? extends Event> o, EventHandler m) {
		if (eventhandlers.get(o) == null) {
			eventhandlers.put(o, new ArrayList<>());
		}
		eventhandlers.get(o).add(m);
	}

	@SuppressWarnings("unchecked")
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

	public static void clean() {
		eventhandlers = null;
		init = false;
		if (threadpool != null) {
			threadpool.shutdownNow();
			threadpool = null;
		}
		if (submitterpool != null) {
			submitterpool.shutdownNow();
			submitterpool = null;
		}
	}
}
