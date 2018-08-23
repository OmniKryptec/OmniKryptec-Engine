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
