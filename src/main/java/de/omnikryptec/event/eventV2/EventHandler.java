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

import java.lang.reflect.Method;

public class EventHandler {
	
	private Object handler;
	private Method m;
	
	public EventHandler(Object handler, Method m) {
		this.handler = handler;
		this.m = m;
	}
	
	public Object getHandler() {
		return handler;
	}
	
	public Method getMethod() {
		return m;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj instanceof EventHandler) {
			EventHandler sec = (EventHandler)obj;
			if(sec.m.equals(m)) {
				return true;
			}
		}
		return false;
	}
	
}