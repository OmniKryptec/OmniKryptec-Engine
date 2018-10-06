package de.omnikryptec.ecs.family;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.omnikryptec.ecs.entity.Entity;
import de.omnikryptec.util.data.CountingMap;

public class FamilyManager implements IFamilyManager {
	
	private CountingMap<Family> uniqueFilters;
	private ListMultimap<Family, Entity> filteredEntities;
	private ListMultimap<Entity, Family> reverseFilteredEntities;
	
	public FamilyManager() {
		uniqueFilters = new CountingMap<>();
		filteredEntities = ArrayListMultimap.create();
		reverseFilteredEntities = ArrayListMultimap.create();
	}

	@Override
	public List<Entity> getEntitiesFor(Family family) {
		return Collections.unmodifiableList(filteredEntities.get(family));
	}

	@Override
	public FamilyManager addFilter(Family family) {
		uniqueFilters.increment(family);
		return this;
	}

	@Override
	public FamilyManager removeFilter(Family family) {
		if (uniqueFilters.keySet().contains(family)) {
			if (uniqueFilters.decrement(family) == 0) {
				filteredEntities.removeAll(family);
			}
		}
		return this;
	}

	@Override
	public FamilyManager addFilteredEntity(Entity entity) {
		for(Family filter : uniqueFilters.keySet()) {
			if(entity.getFamily().contains(filter)) {
				filteredEntities.put(filter, entity);
				reverseFilteredEntities.put(entity, filter);
			}
		}
		return this;
	}

	@Override
	public FamilyManager removeFilteredEntity(Entity entity) {
		for(Family filter : reverseFilteredEntities.get(entity)) {
			filteredEntities.remove(filter, entity);
		}
		reverseFilteredEntities.removeAll(entity);
		return this;
	}

}
