package de.omnikryptec.ecs;

import java.util.BitSet;

import javax.annotation.Nonnull;

import de.omnikryptec.ecs.component.ComponentType;

/**
 * A utility class to help with families/filters in Entity Component Systems.
 * 
 * @author pcfreak9000
 * @see IECSManager
 */
public class Family {

    /**
     * Creates a new BitSet with all indices set to true if a corresponding
     * {@link ComponentType} is required.
     * 
     * @param types the ComponentTypes
     * @return a new bitset
     */
    @Nonnull
    public static BitSet of(@Nonnull ComponentType... types) {
	BitSet bitset = new BitSet();
	for (ComponentType type : types) {
	    bitset.set(type.getId());
	}
	return bitset;
    }

    /**
     * Checks if all bits that are set to true in the second argument, are also true
     * in the first argument. If a bit in the second argument is zero, it will be
     * ignored.
     * 
     * @param input        the bitset that is to be checked
     * @param trueRequired the filter
     * @return true if all bits that are set to true in trueRequired are also true
     *         in input.
     */
    public static boolean containsTrueBits(@Nonnull BitSet input, @Nonnull BitSet trueRequired) {
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
