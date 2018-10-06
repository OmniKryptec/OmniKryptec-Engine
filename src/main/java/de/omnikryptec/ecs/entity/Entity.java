package de.omnikryptec.ecs.entity;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.family.Family;
import de.omnikryptec.util.data.DynamicArray;
import de.omnikryptec.util.data.SimpleDynamicArray;

public class Entity {
    
	private SimpleDynamicArray<Component> components;
	private Family family;
	
    public Entity() {
        this.components = new SimpleDynamicArray<>();
        this.family = new Family();
    }

    public Entity addComponent(Component component) {
    	ComponentType type = ComponentType.of(component.getClass());
    	this.components.set(type.getId(), component);
    	this.family.add(type);
    	return this;
    }
    
    public Entity removeComponent(Component component) {
    	ComponentType type = ComponentType.ofExisting(component.getClass());
    	this.components.set(type.getId(), null);
    	this.family.remove(type);
    	return this;
    }
    
	public <C extends Component>C getComponent(ComponentType componentType) {
		return (C) components.get(componentType.getId());
	}

	public boolean hasComponent(ComponentType type) {
		return family.getBits().get(type.getId());
	}

	public Family getFamily() {
		return family;
	}
    
}
