package de.omnikryptec.ecs;

import java.util.BitSet;

public class AdvancedBitSet extends BitSet {

	private static final long serialVersionUID = -6865824090238135923L;

	public boolean contains(BitSet other) {
		int i = other.nextSetBit(0);
		while (i != -1) {
			if (!get(i)) {
				return false;
			}
			i = other.nextSetBit(i+1);
		}
		return true;
	}
}
