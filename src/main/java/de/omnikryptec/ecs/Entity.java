package de.omnikryptec.ecs;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.ecs.component.ComponentType;

public class Entity {
    
	private Map<ComponentType, Component> components;
	private BitSet bitset;
	
    public Entity() {
        this.components = new HashMap<>();
        this.bitset = new BitSet();
    }

    public Entity addComponent(Component component) {
    	ComponentType type = ComponentType.of(component.getClass());
    	this.components.put(type, component);
    	this.bitset.set(type.getId());
    	return this;
    }
    
	public <C extends Component>C getComponent(ComponentType componentType) {
		return (C) components.get(componentType);
	}
    
}
