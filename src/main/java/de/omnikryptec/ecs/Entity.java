package de.omnikryptec.ecs;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	
	public final long ID;
	private Map<Integer, Component> comps;
	
	public Entity() {
		this.ID = 0;
		this.comps = new HashMap<>();
	}
	
	public Map<Integer, Component> getComponents(){
		return comps;
	}
}
