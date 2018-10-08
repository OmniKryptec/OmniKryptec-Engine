package de.omnikryptec.ecs.entity;

import java.util.BitSet;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.util.data.DynamicArray;

public class Entity {
    
	private DynamicArray<Component> componentsArray;
	private BitSet components;
	//TODO better way of accessing and changing this (maybe an AbstractEntityManager?)
	protected IEntityManager entityManager;
	
    public Entity() {
        this.componentsArray = new DynamicArray<>();
        this.components = new BitSet();
    }

    public Entity addComponent(Component component) {
    	ComponentType type = ComponentType.of(component.getClass());
    	return addComponent(type, component);
    }
    
    public Entity addComponent(ComponentType type, Component component) {
    	this.componentsArray.set(type.getId(), component);
    	this.components.set(type.getId());
    	if(hasEntityManager()) {
    		this.entityManager.updateFilteredEntity(this);
    	}
    	return this;
    }
    
    public Entity removeComponent(Component component) {
    	ComponentType type = ComponentType.ofExisting(component.getClass());
    	return removeComponent(type);
    }
    
    public Entity removeComponent(ComponentType componentType) {
    	this.componentsArray.set(componentType.getId(), null);
    	this.components.clear(componentType.getId());
    	if(hasEntityManager()) {
    		this.entityManager.updateFilteredEntity(this);
    	}
    	return this;
    }
    
	public <C extends Component>C getComponent(ComponentType componentType) {
		return (C) componentsArray.get(componentType.getId());
	}

	public boolean hasComponent(ComponentType type) {
		return components.get(type.getId());
	}
	
	public BitSet getComponents() {
		return components;
	}
    
	private boolean hasEntityManager() {
		return entityManager != null;
	}
	
}
