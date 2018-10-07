package de.omnikryptec.ecs.entity;

import java.util.BitSet;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.util.data.DynamicArray;

public class Entity {
    
	private DynamicArray<Component> components;
	private BitSet family;
	IEntityManager entityManager;
	
    public Entity() {
        this.components = new DynamicArray<>();
        this.family = new BitSet();
    }

    public Entity addComponent(Component component) {
    	ComponentType type = ComponentType.of(component.getClass());
    	return addComponent(type, component);
    }
    
    public Entity addComponent(ComponentType type, Component component) {
    	this.components.set(type.getId(), component);
    	this.family.set(type.getId());
    	return this;
    }
    
    public Entity removeComponent(Component component) {
    	ComponentType type = ComponentType.ofExisting(component.getClass());
    	return removeComponent(type);
    }
    
    public Entity removeComponent(ComponentType componentType) {
    	this.components.set(componentType.getId(), null);
    	this.family.clear(componentType.getId());
    	return this;
    }
    
	public <C extends Component>C getComponent(ComponentType componentType) {
		return (C) components.get(componentType.getId());
	}

	public boolean hasComponent(ComponentType type) {
		return family.get(type.getId());
	}

	public BitSet getFamily() {
		return family;
	}
    
}
