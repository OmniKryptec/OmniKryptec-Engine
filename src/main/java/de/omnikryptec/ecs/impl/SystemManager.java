package de.omnikryptec.ecs.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import de.omnikryptec.ecs.system.ComponentSystem;

public class SystemManager{
	
	private Collection<ComponentSystem> systems;
	private Collection<ComponentSystem> unmodifiableSystems;
	
	public SystemManager() {
		//TODO Use comparable/ordered collection for priorities?  
		this.systems = new HashSet<>();
		this.unmodifiableSystems = Collections.unmodifiableCollection(this.systems);
	}
	
	public SystemManager addSystem(ComponentSystem system) {
		systems.add(system);
		return this;
	}

	public SystemManager removeSystem(ComponentSystem system) {
		systems.remove(system);
		return this;
	}

	public Collection<ComponentSystem> getAll() {
		return unmodifiableSystems;
	}

}
