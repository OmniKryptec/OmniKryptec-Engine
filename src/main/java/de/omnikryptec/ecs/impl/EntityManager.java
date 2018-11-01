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
    
    private Multimap<BitSet, EntityListener> familyListeners;
    
    private Collection<Entity> entities;
    private Collection<Entity> unmodifiableEntities;
    
    private CountingMap<BitSet> uniqueFilters;
    private ListMultimap<BitSet, Entity> filteredEntities;
    private ListMultimap<Entity, BitSet> reverseFilteredEntities;
    
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
    
    public EntityManager addEntity(Entity entity) {
        if (entities.add(entity)) {
            if (!entity.getComponents().isEmpty()) {
                for (BitSet filter : uniqueFilters.keySet()) {
                    if (Family.containsTrueBits(entity.getComponents(), filter)) {
                        filteredEntities.put(filter, entity);
                        reverseFilteredEntities.put(entity, filter);
                        for (EntityListener l : familyListeners.get(filter)) {
                            l.entityAdded(entity);
                        }
                    }
                }
            }
        }
        return this;
    }
    
    public EntityManager removeEntity(Entity entity) {
        if (entities.remove(entity)) {
            for (BitSet filter : reverseFilteredEntities.get(entity)) {
                filteredEntities.remove(filter, entity);
            }
            reverseFilteredEntities.removeAll(entity);
        }
        return this;
    }
    
    public Collection<Entity> getAll() {
        return unmodifiableEntities;
    }
    
    public List<Entity> getEntitiesFor(BitSet family) {
        return Collections.unmodifiableList(filteredEntities.get(family));
    }
    
    public EntityManager addFilter(BitSet family) {
        if (family.isEmpty()) {
            throw new IllegalArgumentException("Empty family");
        }
        if (uniqueFilters.increment(family) == 1) {
            for (Entity e : entities) {
                if (Family.containsTrueBits(e.getComponents(), family)) {
                    filteredEntities.put(family, e);
                    reverseFilteredEntities.put(e, family);
                    for (EntityListener l : familyListeners.get(family)) {
                        l.entityAdded(e);
                    }
                }
            }
        }
        return this;
    }
    
    public EntityManager removeFilter(BitSet family) {
        if (family.isEmpty()) {
            throw new IllegalArgumentException("Empty family");
        }
        if (uniqueFilters.keySet().contains(family)) {
            if (uniqueFilters.decrement(family) == 0) {
                List<Entity> removed = filteredEntities.removeAll(family);
                Collection<EntityListener> listeners = familyListeners.get(family);
                for (EntityListener l : listeners) {
                    for (Entity e : removed) {
                        l.entityRemoved(e);
                    }
                }
            }
        }
        return this;
    }
    
    public EntityManager updateEntityFamilyStatus(Entity entity) {
        for (BitSet family : uniqueFilters) {
            boolean niceFamily = Family.containsTrueBits(entity.getComponents(), family);
            boolean alreadySet = reverseFilteredEntities.containsEntry(entity, family);
            if (niceFamily && !alreadySet) {
                reverseFilteredEntities.put(entity, family);
                filteredEntities.put(family, entity);
                for (EntityListener l : familyListeners.get(family)) {
                    l.entityAdded(entity);
                }
            } else if (!niceFamily && alreadySet) {
                reverseFilteredEntities.remove(entity, family);
                filteredEntities.remove(family, entity);
                for (EntityListener l : familyListeners.get(family)) {
                    l.entityRemoved(entity);
                }
            }
        }
        return this;
    }
    
    public void addEntityListener(BitSet family, EntityListener listener) {
        familyListeners.put(family, listener);
    }
    
    public void removeEnityListener(BitSet family, EntityListener listener) {
        familyListeners.remove(family, listener);
    }
    
}
