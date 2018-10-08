package de.omnikryptec.ecs.impl;

import java.util.BitSet;

import de.omnikryptec.ecs.component.ComponentType;

public class Family {

	public static BitSet of(ComponentType... types) {
		BitSet bitset = new BitSet();
		for (ComponentType type : types) {
			bitset.set(type.getId());
		}
		return bitset;
	}

	/**
	 * 
	 * 
	 * @param input        the bitset that is to be checked
	 * @param trueRequired the filter
	 * @return true if all bits that are set to true in trueRequired are also true in input.
	 */
	public static boolean containsTrueBits(BitSet input, BitSet trueRequired) {
		int i = trueRequired.nextSetBit(0);
		while (i != -1) {
			if (!input.get(i)) {
				return false;
			}
			i = trueRequired.nextSetBit(i + 1);
		}
		return true;
	}

}
