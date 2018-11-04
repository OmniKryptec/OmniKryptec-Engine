/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.ecs;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.ecs.impl.ECSManager;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.util.UnsupportedOperationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

//TODO is updateable useful here?

/**
 * An interface describing a general Entity Component System (ECS) consisting of
 * {@link Entity}s, {@link ComponentSystem}s, {@link Component}s, and atleast
 * one Manager, an {@link IECSManager}.<br>
 * <br>
 * In an ECS, Entities usually are only containers of Components. Components
 * usually only hold Data. That means that usually both of them do not contain
 * any game logic. <br>
 * ComponentSystems usually only contain logic and process only Entities that
 * have certain Components. Entities with the same type of Components can be
 * grouped in {@link Family}s (that might improve performance).
 *
 * @author pcfreak9000
 */
@ParametersAreNonnullByDefault
public interface IECSManager extends Updateable {

    /**
     * the default implementation of an {@link IECSManager}. This is the
     * {@link ECSManager}.<br>
     * Supports all operations.
     *
     * @return new instance of the default IECSManager
     */
    @Nonnull
    public static IECSManager createDefault() {
        return new ECSManager();
    }

    /**
     * Adds an {@link Entity} to this {@link IECSManager}.
     *
     * @param entity the entity to add
     */
    void addEntity(Entity entity);

    /**
     * Removes an {@link Entity} from this {@link IECSManager}
     *
     * @param entity the entity to remove
     */
    void removeEntity(Entity entity);

    /**
     * Adds a {@link ComponentSystem} to this {@link IECSManager}.
     *
     * @param system the system to add
     */
    void addSystem(ComponentSystem system);

    /**
     * Removes a {@link ComponentSystem} from this {@link IECSManager}.
     *
     * @param system the system to remove
     */
    void removeSystem(ComponentSystem system);

    /**
     * Gets all the entities matching a certain {@link Family}.
     *
     * @param f the family required
     *
     * @return a list of entities matching the requirements
     */
    @Nonnull
    List<Entity> getEntitesFor(BitSet f);

    /**
     * All via {@link #addEntity(Entity)} added entities.
     *
     * @return all entities added to this IECSManager
     */
    @Nonnull
    Collection<Entity> getAll();

    /**
     * Called by the entity after it's {@link Component}s changed.<br>
     * This method will most likely only be used internaly by the {@link Entity}.
     *
     * @param entity the entity those Components changed
     *
     * @throws UnsupportedOperationException if this operation is not supported by
     *                                       the implementation
     */
    void onEntityComponentsChanged(Entity entity) throws UnsupportedOperationException;

    /**
     * Add an {@link EntityListener} to this {@link IECSManager}.<br>
     * <br>
     * If the family is null, the listener will be registered for general entity
     * events.<br>
     * If the family is not null, the listener will be registered for entity events
     * of this specific family.
     *
     * @param family   the family, or null
     * @param listener the entitylistener
     *
     * @throws UnsupportedOperationException if this operation is not supported by
     *                                       the implementation
     */
    void addEntityListener(@Nullable BitSet family, EntityListener listener) throws UnsupportedOperationException;

    /**
     * Remove an {@link EntityListener} from this {@link IECSManager}.<br>
     * <br>
     * If the family is null, the listener will be removed from general entity
     * events.<br>
     * If the family is not null, the listener will be removed from entity events of
     * this specific family.
     *
     * @param family   the family, or null
     * @param listener the entitylistener
     *
     * @throws UnsupportedOperationException if this operation is not supported by
     *                                       the implementation
     */
    void removeEntityListener(@Nullable BitSet family, EntityListener listener) throws UnsupportedOperationException;

}
