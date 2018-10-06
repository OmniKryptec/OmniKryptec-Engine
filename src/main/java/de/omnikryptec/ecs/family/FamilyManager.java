package de.omnikryptec.ecs.family;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;

import de.omnikryptec.ecs.entity.Entity;

public class FamilyManager implements IFamilyManager {

	private Multiset<Family> uniqueFilters;
	private ListMultimap<Family, Entity> filteredEntities;

	public FamilyManager() {
		uniqueFilters = HashMultiset.create();
		filteredEntities = ArrayListMultimap.create();
	}

	@Override
	public List<Entity> getEntitiesFor(Family family) {
		return Collections.unmodifiableList(filteredEntities.get(family));
	}

	@Override
	public FamilyManager addFilter(Family family) {
		uniqueFilters.add(family);
		return this;
	}

	@Override
	public FamilyManager removeFilter(Family family) {
		if (uniqueFilters.contains(family)) {
			uniqueFilters.remove(family);
			if (uniqueFilters.count(family) == 0) {
				filteredEntities.removeAll(family);
			}
		}
		return this;
	}

	@Override
	public FamilyManager addFilteredEntity(Entity entity) {
		for(Family filter : uniqueFilters.elementSet()) {
			if(entity.getFamily().contains(filter)) {
				filteredEntities.put(filter, entity);
			}
		}
		return this;
	}

	@Override
	public FamilyManager removeFilteredEntity(Entity entity) {
		//TODO
		return this;
	}

}
