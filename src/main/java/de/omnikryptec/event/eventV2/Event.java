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

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Event {

	private boolean called = false;
	private final AtomicBoolean consumed = new AtomicBoolean(false);
	protected boolean asyncSubmission = false;
	protected boolean asyncExecution = false;
	protected boolean consumeable = true;
	private EventBus bus;
	
	protected Event(EventBus poster) {
		this.bus = poster;
	}
	
	public void call() {
		if (called == true) {
			return;
		}
		called = true;
		bus.submit(this);
	}

	public Event consume() {
		if(isConsumeable()) {
			consumed.set(true);
		}
		return this;
	}

	public EventBus getBus() {
		return bus;
	}
	
	public boolean isConsumed() {
		return consumed.get();
	}

	public boolean isCalled() {
		return called;
	}

	public Event setAsyncSubmission(boolean b) {
		this.asyncSubmission = b;
		return this;
	}

	public Event setAsyncExecution(boolean b) {
		this.asyncExecution = b;
		return this;
	}

	public boolean isAsyncSubmission() {
		return asyncSubmission;
	}

	public boolean isAsyncExecution() {
		return asyncExecution;
	}

	public Event setConsumeable(boolean b) {
		this.consumeable = b;
		return this;
	}

	public boolean isConsumeable() {
		return consumeable;
	}
	
	public void beforeExecution(EventHandler handler) {
	}

}
