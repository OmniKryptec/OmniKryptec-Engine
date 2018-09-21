package de.omnikryptec.ecs;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.ecs.component.ComponentType;

public class Entity {
    
	private Map<ComponentType, Component> components;
	
    public Entity() {
        this.components = new HashMap<>();
    }

    public Entity addComponent(Component component) {
    	this.components.put(ComponentType.of(component.getClass()), component);
    	return this;
    }
    
	public <C extends Component>C getComponent(ComponentType componentType) {
		return (C) components.get(componentType);
	}
    
}
