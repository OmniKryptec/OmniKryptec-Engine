/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.ecs.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityListener;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.util.data.CountingMap;

import java.util.*;

public class EntityManager {
    
    private final Multimap<BitSet, EntityListener> familyListeners;
    
    private final Collection<Entity> entities;
    private final Collection<Entity> unmodifiableEntities;
    
    private final CountingMap<BitSet> uniqueFilters;
    private final ListMultimap<BitSet, Entity> filteredEntities;
    private final ListMultimap<Entity, BitSet> reverseFilteredEntities;
    
    public EntityManager() {
        // HashSet is a few microsecs slower than ArrayList on average but when removing
        // entities its twice as fast but what about adding entities?!?!?!
        this.entities = new HashSet<>();
        this.unmodifiableEntities = Collections.unmodifiableCollection(this.entities);
        this.uniqueFilters = new CountingMap<>();
        this.filteredEntities = ArrayListMultimap.create();
        this.reverseFilteredEntities = ArrayListMultimap.create();
        this.familyListeners = ArrayListMultimap.create();
    }
    
    public EntityManager addEntity(final Entity entity) {
        if (this.entities.add(entity)) {
            if (!entity.getComponents().isEmpty()) {
                for (final BitSet filter : this.uniqueFilters.keySet()) {
                    if (Family.containsTrueBits(entity.getComponents(), filter)) {
                        this.filteredEntities.put(filter, entity);
                        this.reverseFilteredEntities.put(entity, filter);
                        for (final EntityListener l : this.familyListeners.get(filter)) {
                            l.entityAdded(entity);
                        }
                    }
                }
            }
        }
        return this;
    }
    
    public EntityManager removeEntity(final Entity entity) {
        if (this.entities.remove(entity)) {
            for (final BitSet filter : this.reverseFilteredEntities.get(entity)) {
                this.filteredEntities.remove(filter, entity);
            }
            this.reverseFilteredEntities.removeAll(entity);
        }
        return this;
    }
    
    public Collection<Entity> getAll() {
        return this.unmodifiableEntities;
    }
    
    public List<Entity> getEntitiesFor(final BitSet family) {
        return Collections.unmodifiableList(this.filteredEntities.get(family));
    }
    
    public EntityManager addFilter(final BitSet family) {
        if (family.isEmpty()) {
            throw new IllegalArgumentException("Empty family");
        }
        if (this.uniqueFilters.increment(family) == 1) {
            for (final Entity e : this.entities) {
                if (Family.containsTrueBits(e.getComponents(), family)) {
                    this.filteredEntities.put(family, e);
                    this.reverseFilteredEntities.put(e, family);
                    for (final EntityListener l : this.familyListeners.get(family)) {
                        l.entityAdded(e);
                    }
                }
            }
        }
        return this;
    }
    
    public EntityManager removeFilter(final BitSet family) {
        if (family.isEmpty()) {
            throw new IllegalArgumentException("Empty family");
        }
        if (this.uniqueFilters.keySet().contains(family)) {
            if (this.uniqueFilters.decrement(family) == 0) {
                final List<Entity> removed = this.filteredEntities.removeAll(family);
                final Collection<EntityListener> listeners = this.familyListeners.get(family);
                for (final EntityListener l : listeners) {
                    for (final Entity e : removed) {
                        l.entityRemoved(e);
                    }
                }
            }
        }
        return this;
    }
    
    public EntityManager updateEntityFamilyStatus(final Entity entity) {
        for (final BitSet family : this.uniqueFilters) {
            final boolean niceFamily = Family.containsTrueBits(entity.getComponents(), family);
            final boolean alreadySet = this.reverseFilteredEntities.containsEntry(entity, family);
            if (niceFamily && !alreadySet) {
                this.reverseFilteredEntities.put(entity, family);
                this.filteredEntities.put(family, entity);
                for (final EntityListener l : this.familyListeners.get(family)) {
                    l.entityAdded(entity);
                }
            } else if (!niceFamily && alreadySet) {
                this.reverseFilteredEntities.remove(entity, family);
                this.filteredEntities.remove(family, entity);
                for (final EntityListener l : this.familyListeners.get(family)) {
                    l.entityRemoved(entity);
                }
            }
        }
        return this;
    }
    
    public void addEntityListener(final BitSet family, final EntityListener listener) {
        this.familyListeners.put(family, listener);
    }
    
    public void removeEnityListener(final BitSet family, final EntityListener listener) {
        this.familyListeners.remove(family, listener);
    }
    
}
