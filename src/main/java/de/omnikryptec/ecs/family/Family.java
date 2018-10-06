package de.omnikryptec.ecs.family;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.util.data.AdvancedBitSet;

public class Family {

	private AdvancedBitSet bitSet;

	public Family(ComponentType... types) {
		this.bitSet = new AdvancedBitSet();
		add(types);
	}

	public Family(Entity entity) {
		this.bitSet = (AdvancedBitSet) entity.getFamily().getBits().clone();
	}
	
	public void remove(ComponentType... types) {
		for (ComponentType t : types) {
			this.bitSet.clear(t.getId());
		}
	}

	public void add(ComponentType... types) {
		for (ComponentType t : types) {
			this.bitSet.set(t.getId());
		}
	}

	public boolean contains(Family family) {
		return bitSet.contains(family.bitSet);
	}

	public AdvancedBitSet getBits() {
		return bitSet;
	}
	
	@Override
	public int hashCode() {
		return bitSet.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof Family) {
			return ((Family) obj).bitSet.equals(bitSet);
		}
		return false;
	}

}
