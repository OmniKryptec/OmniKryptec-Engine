/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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
import com.google.common.collect.ListMultimap;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.util.updater.Time;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventBus implements IUpdatable, IEventListener {

    private static final Comparator<IEventListener> LISTENER_COMP = new Comparator<IEventListener>() {

        @Override
        public int compare(IEventListener o1, IEventListener o2) {

            return (int) Math.signum(o2.priority() - o1.priority());
        }
    };

    private static class ObjMapping {
        private final Class<? extends Event> eventType;
        private final IEventListener handler;

        private ObjMapping(IEventListener handler, Class<? extends Event> eventType) {
            this.eventType = eventType;
            this.handler = handler;
        }
    }

    private final AtomicBoolean processing = new AtomicBoolean(false);
    private final ListMultimap<Class<? extends Event>, IEventListener> listeners;
    private final ListMultimap<Object, ObjMapping> objMappings;
    private final Queue<Event> eventQueue;
    private boolean receiveConsumed = true;

    /**
     * A read-only version of this {@link EventBus}
     */
    public final ReadableEventBus READ_ONLY = new ReadableEventBus() {

        @Override
        public void register(final Object object) {
            EventBus.this.register(object);
        }

        @Override
        public void register(final IEventListener listener, final Class<? extends Event> eventtype) {
            EventBus.this.register(listener, eventtype);
        }

        @Override
        public void post(final Event event) {
            EventBus.this.post(event);
        }

        @Override
        public void enqueue(final Event event) {
            EventBus.this.enqueue(event);
        }
    };

    public EventBus() {
        this(false);
    }

    public EventBus(boolean concurrent) {
        this.eventQueue = concurrent ? new ConcurrentLinkedQueue<>() : new ArrayDeque<>();
        this.listeners = ArrayListMultimap.create();
        this.objMappings = ArrayListMultimap.create();
    }

    public void unregister(IEventListener listener) {
        unregister(listener, Event.class);
    }

    public void unregister(IEventListener listener, Class<? extends Event> eventtype) {
        List<IEventListener> list = listeners.get(eventtype);
        list.remove(listener);
    }

    public void unregister(Object object) {
        List<ObjMapping> list = objMappings.removeAll(object);
        for (ObjMapping ma : list) {
            unregister(ma.handler, ma.eventType);
        }
    }

    /**
     * Registers an {@link IEventListener} to ALL events.<br>
     * Useful when chaining together multiple {@link EventBus}.
     *
     * <p>
     * Note: {@link EventSubscription}s are ignored.
     * </p>
     *
     * @param listener the listener
     */
    public void register(final IEventListener listener) {
        register(listener, Event.class);
    }

    /**
     * Registers an {@link IEventListener} to a certain type of event.
     * <p>
     * Note: {@link EventSubscription}s are ignored.
     * </p>
     *
     * @param listener  the listener
     * @param eventtype the class of the event type
     */
    public void register(final IEventListener listener, final Class<? extends Event> eventtype) {
        List<IEventListener> list = listeners.get(eventtype);
        list.add(listener);
        list.sort(LISTENER_COMP);
    }

    /**
     * Registers any {@link EventSubscription}s found in this object:
     * <ul>
     * <li>If the object is a class object, every static method of that class object
     * that is subscribed to an event will be added to this {@link EventBus}. Any
     * non-static method that is subscribed to an event in that class will throw an
     * exception.</li>
     * <li>If the object is an object every method that is subscribed to an event
     * (including static ones) will be added to this {@link EventBus}.</li>
     * </ul>
     * <p>
     * Note: If the object does not contain any subscriptions, an exception is
     * thrown.
     * </p>
     *
     * @param object the object to search for subscriptions in
     */
    public void register(Object object) {
        Method[] methods;
        if (object instanceof Class<?>) {
            methods = ((Class<?>) object).getDeclaredMethods();
            object = null;
        } else {
            methods = object.getClass().getDeclaredMethods();
        }
        boolean annotationFound = false;
        for (final Method m : methods) {
            if (object == null && !Modifier.isStatic(m.getModifiers())) {
                throw new IllegalArgumentException("Nonstatic event listener registered by class");
            }
            final EventSubscription esub = m.getAnnotation(EventSubscription.class);
            if (esub != null) {
                annotationFound = true;
                final EventHandler handler = new EventHandler(object, m, esub.receiveConsumed(), esub.priority());
                final Class<?>[] cls = m.getParameterTypes();
                if (cls.length != 1) {
                    throw new IllegalArgumentException("Wrong amount of parameter types in event listener: " + object);
                } else {
                    if (Event.class.isAssignableFrom(cls[0])) {
                        if (this.listeners.containsKey(cls[0]) && this.listeners.containsEntry(cls[0], handler)) {
                            continue;
                        }
                        Class<? extends Event> eventType = (Class<? extends Event>) cls[0];
                        ObjMapping ma = new ObjMapping(handler, eventType);
                        objMappings.put(object, ma);
                        register(handler, eventType);
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

    public void post(final Event event) {
        processEvent(event);
    }

    public void enqueue(final Event event) {
        this.eventQueue.add(event);
    }

    public void processQueuedEvents() {
        if (this.processing.get()) {
            throw new IllegalStateException("Already processing!");
        }
        this.processing.set(true);
        while (!this.eventQueue.isEmpty()) {
            processEvent(this.eventQueue.poll());
        }
        this.processing.set(false);
    }

    public boolean isProcessing() {
        return this.processing.get();
    }

    private void processEvent(final Event event) {
        Class<?> someclazz = event.getClass();
        do {
            for (final IEventListener listener : this.listeners.get((Class<? extends Event>) someclazz)) {
                if (!event.isConsumeable() || !event.isConsumed() || listener.receiveConsumed()) {
                    listener.invoke(event);
                }
            }
            someclazz = someclazz.getSuperclass();
        } while (event.triggersSuperEventListeners() && someclazz != Object.class && someclazz != null);
    }

    @Override
    public void update(final Time time) {
        processQueuedEvents();
    }

    @Override
    public void invoke(final Event ev) {
        processEvent(ev);
    }

    @Override
    public float priority() {
        return Float.NEGATIVE_INFINITY;
    }

    @Override
    public boolean receiveConsumed() {
        return receiveConsumed;
    }

    public void setReceiveConsumed(boolean b) {
        this.receiveConsumed = b;
    }

    @Override
    public boolean passive() {

        return false;
    }

}
