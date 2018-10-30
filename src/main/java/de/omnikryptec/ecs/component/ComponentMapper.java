package de.omnikryptec.ecs.component;

import de.omnikryptec.ecs.Entity;

public class ComponentMapper<C extends Component> {

    private final ComponentType componentType;

    public ComponentMapper(Class<C> componentClass) {
	this.componentType = ComponentType.of(componentClass);
    }

    public C get(Entity entity) {
	return entity.getComponent(componentType);
    }

    public ComponentType getType() {
	return componentType;
    }
}
