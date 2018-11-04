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

package de.omnikryptec.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.util.updater.Time;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventBus implements Updateable {

    private final AtomicBoolean processing = new AtomicBoolean(false);
    private Multimap<Class<? extends Event>, IEventListener> listeners;
    private Queue<Event> eventQueue;

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

    @Override
    public void update(Time time) {
        processQueuedEvents();
    }
}
