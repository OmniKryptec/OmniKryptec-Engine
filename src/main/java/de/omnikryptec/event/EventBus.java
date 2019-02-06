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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.util.updater.Time;

public class EventBus implements Updateable, IEventListener {
    
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private final Multimap<Class<? extends Event>, IEventListener> listeners;
    private final Queue<Event> eventQueue;
    
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
        this.eventQueue = new ConcurrentLinkedQueue<>();
        this.listeners = ArrayListMultimap.create();
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
        this.listeners.put(eventtype, listener);
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
                final EventHandler handler = new EventHandler(object, m, esub.receiveConsumed());
                final Class<?>[] cls = m.getParameterTypes();
                if (cls.length != 1) {
                    throw new IllegalArgumentException("Wrong amount of parameter types in event listener: " + object);
                } else {
                    if (Event.class.isAssignableFrom(cls[0])) {
                        if (this.listeners.containsKey(cls[0]) && this.listeners.containsEntry(cls[0], handler)) {
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
    
    //TODO change this to regular update?
    @Override
    public void postUpdate(final Time time) {
        processQueuedEvents();
    }
    
    //TODO receive consumed events?
    @Override
    public void invoke(Event ev) {
        post(ev);
    }
}
