package de.omnikryptec.ecs.entity;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.family.Family;

public class Entity {
    
	private Map<ComponentType, Component> components;
	private Family family;
	
    public Entity() {
        this.components = new HashMap<>();
        this.family = new Family();
    }

    public Entity addComponent(Component component) {
    	ComponentType type = ComponentType.of(component.getClass());
    	this.components.put(type, component);
    	this.family.add(type);
    	return this;
    }
    
    public Entity removeComponent(Component component) {
    	ComponentType type = ComponentType.ofExisting(component.getClass());
    	this.components.remove(type);
    	this.family.remove(type);
    	return this;
    }
    
	public <C extends Component>C getComponent(ComponentType componentType) {
		return (C) components.get(componentType);
	}

	public boolean hasComponent(ComponentType type) {
		return family.getBits().get(type.getId());
	}

	public Family getFamily() {
		return family;
	}
    
}
