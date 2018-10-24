package de.omnikryptec.ecs;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.ecs.impl.ECSManager;
import de.omnikryptec.ecs.system.ComponentSystem;

//TODO is updateable useful here?
public interface IECSManager extends Updateable {

	// TODO useful or too much?
	public static IECSManager createDefault() {
		return new ECSManager();
	}

	/**
	 * Adds an {@link Entity} to this {@link IECSManager}.
	 * 
	 * @param entity the entity to add
	 */
	void addEntity(@Nonnull Entity entity);

	/**
	 * Removes an {@link Entity} from this {@link IECSManager}
	 * 
	 * @param entity the entity to remove
	 */
	void removeEntity(@Nonnull Entity entity);

	/**
	 * Adds a {@link ComponentSystem} to this {@link IECSManager}.
	 * 
	 * @param system the system to add
	 */
	void addSystem(@Nonnull ComponentSystem system);

	/**
	 * Removes a {@link ComponentSystem} from this {@link IECSManager}.
	 * 
	 * @param system the system to remove
	 */
	void removeSystem(@Nonnull ComponentSystem system);

	/**
	 * Gets all the entities matching a certain {@link Family}.
	 * 
	 * @param f the family required
	 * @return a list of entities matching the requirements
	 */
	List<Entity> getEntitesFor(@Nonnull BitSet f);

	/**
	 * All via {@link #addEntity(Entity)} added entities.
	 * 
	 * @return all entities added to this IECSManager
	 */
	Collection<Entity> getAll();

	/**
	 * Called by the entity after it's {@link Component}s changed.<br>
	 * This method will most likely only be used internaly by the {@link Entity}.
	 * 
	 * @param entity the entity those Components changed
	 */
	void onEntityComponentsChanged(@Nonnull Entity entity);

	void addEntityListener(@Nullable BitSet family, @Nonnull EntityListener listener);

	void removeEntityListener(@Nullable BitSet family, @Nonnull EntityListener listener);

}
