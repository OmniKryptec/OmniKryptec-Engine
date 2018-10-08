package de.omnikryptec.ecs.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.util.data.CountingMap;

public class EntityManager {

	
	private Collection<Entity> entities;
	private Collection<Entity> unmodifiableEntities;

	private CountingMap<BitSet> uniqueFilters;
	private ListMultimap<BitSet, Entity> filteredEntities;
	private ListMultimap<Entity, BitSet> reverseFilteredEntities;

	public EntityManager() {
		// TODO Set or List?!
		this.entities = new ArrayList<>();
		this.unmodifiableEntities = Collections.unmodifiableCollection(this.entities);
		this.uniqueFilters = new CountingMap<>();
		this.filteredEntities = ArrayListMultimap.create();
		this.reverseFilteredEntities = ArrayListMultimap.create();
	}

	public EntityManager addEntity(Entity entity) {
		entities.add(entity);
		for (BitSet filter : uniqueFilters.keySet()) {
			if (Family.containsTrueBits(entity.getComponents(), filter)) {
				filteredEntities.put(filter, entity);
				reverseFilteredEntities.put(entity, filter);
			}
		}
		return this;
	}

	public EntityManager removeEntity(Entity entity) {
		entities.remove(entity);
		for (BitSet filter : reverseFilteredEntities.get(entity)) {
			filteredEntities.remove(filter, entity);
		}
		reverseFilteredEntities.removeAll(entity);
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
				filteredEntities.removeAll(family);
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
			} else if (!niceFamily && alreadySet) {
				reverseFilteredEntities.remove(entity, family);
				filteredEntities.remove(family, entity);
			}
		}
		return this;
	}

}
