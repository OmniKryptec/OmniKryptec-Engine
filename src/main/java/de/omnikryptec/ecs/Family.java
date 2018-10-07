package de.omnikryptec.ecs;

import java.util.BitSet;

import de.omnikryptec.ecs.component.ComponentType;

public class Family {

	public static BitSet of(ComponentType ...types) {
		BitSet bitset = new BitSet();
		for(ComponentType type : types) {
			bitset.set(type.getId());
		}
		return bitset;
	}
	
	public static boolean contains(BitSet input, BitSet other) {
		int i = other.nextSetBit(0);
		while (i != -1) {
			if (!input.get(i)) {
				return false;
			}
			i = other.nextSetBit(i+1);
		}
		return true;
	}

}
