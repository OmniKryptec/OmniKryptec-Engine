package de.omnikryptec.ecs.system;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class SystemManager implements ISystemManager{
	
	private Collection<ComponentSystem> systems;
	private Collection<ComponentSystem> unmodifiableSystems;
	
	public SystemManager() {
		//TODO Use comparable/ordered collection for priorities?  
		this.systems = new HashSet<>();
		this.unmodifiableSystems = Collections.unmodifiableCollection(this.systems);
	}
	
	@Override
	public SystemManager addSystem(ComponentSystem system) {
		systems.add(system);
		return this;
	}

	@Override
	public SystemManager removeSystem(ComponentSystem system) {
		systems.remove(system);
		return this;
	}

	@Override
	public Collection<ComponentSystem> getAll() {
		return unmodifiableSystems;
	}

}
