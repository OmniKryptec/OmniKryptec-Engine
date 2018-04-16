package omnikryptec.event.eventV2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class EventSystem {

	private static Comparator<Method> comp = new Comparator<Method>() {

		@Override
		public int compare(Method o1, Method o2) {
			return (int) Math.signum(o1.getAnnotation(EventSubscription.class).priority()
					- o2.getAnnotation(EventSubscription.class).priority());
		}

	};
	private static HashMap<Class<? extends Event>, List<Method>> eventhandlers = new HashMap<>();
	private static ExecutorService threadpool = Executors.newFixedThreadPool(2);

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

	public static void submit(Event event) {
		if (event.usesCurrentThread()) {
			__submit(event);
		} else {
			threadpool.submit(() -> __submit(event));
		}
	}

	private static void __submit(Event event) {
		List<Method> list = eventhandlers.get(event.getClass());
		for (Method m : list) {
			try {
				m.invoke(null, event);
			} catch (IllegalAccessException e) {
				Logger.log("Could not call eventhandler: " + e, LogLevel.ERROR);
			} catch (IllegalArgumentException e) {
				Logger.logErr("Some IllegalArgumentException: ", e);
			} catch (InvocationTargetException e) {
				Logger.log("Event failed: " + e, LogLevel.ERROR);
				Logger.logErr("", e);
			}
		}
	}

	public static void findEventAnnotations(ClassLoader loader, ClassFilter filter) {
		Iterator<Class<?>> it = list(loader);
		while (it.hasNext()) {
			Class<?> clazz = it.next();
			if (filter.accept(clazz)) {
				registerEventClass(clazz);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void registerEventClass(Class<?> clazz) {
		if (clazz.isAnnotationPresent(EventHandler.class)) {
			Method[] methods = clazz.getDeclaredMethods();
			for (Method m : methods) {
				if (m.isAnnotationPresent(EventSubscription.class)) {
					Class<?>[] cls = m.getParameterTypes();
					if (cls.length != 1) {
						Logger.log("The eventhandler " + clazz + " uses incorrect event-handling methods!",
								LogLevel.WARNING);
					} else {
						if (Event.class.isAssignableFrom(cls[0])) {
							put((Class<? extends Event>) cls[0], m);
						} else {
							Logger.log("Event-handling method is useless, parameter is not an event in " + clazz
									+ ", method " + m.toString(), LogLevel.WARNING);
						}
					}
				}
			}
		}
	}

	private static void put(Class<? extends Event> clazz, Method m) {
		if (eventhandlers.get(clazz) == null) {
			eventhandlers.put(clazz, new ArrayList<>());
		}
		eventhandlers.get(clazz).add(m);
		eventhandlers.get(clazz).sort(comp);
	}

	private static Iterator<Class<?>> list(ClassLoader CL) {
		try {
			Class<?> CL_class = CL.getClass();
			while (CL_class != ClassLoader.class) {
				CL_class = CL_class.getSuperclass();
			}
			Field ClassLoader_classes_field = CL_class.getDeclaredField("classes");
			ClassLoader_classes_field.setAccessible(true);
			@SuppressWarnings("unchecked")
			Vector<Class<?>> classes = (Vector<Class<?>>) ClassLoader_classes_field.get(CL);
			return classes.iterator();
		} catch (Exception e) {
			return null;
		}
	}
}
