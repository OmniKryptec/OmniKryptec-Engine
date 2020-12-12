/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.ecs;

import java.util.BitSet;

import javax.annotation.Nonnull;

import de.omnikryptec.ecs.component.Component;
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
     *
     * @return a new bitset
     */
    @Nonnull
    public static BitSet of(@Nonnull final ComponentType... types) {
        final BitSet bitset = new BitSet();
        for (final ComponentType type : types) {
            bitset.set(type.id);
        }
        return bitset;
    }
    
    public static BitSet of(@Nonnull final Class<? extends Component>... classes) {
        final BitSet bitset = new BitSet();
        for (final Class<? extends Component> type : classes) {
            bitset.set(ComponentType.of(type).id);
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
     *
     * @return true if all bits that are set to true in trueRequired are also true
     *         in input.
     */
    public static boolean containsTrueBits(@Nonnull final BitSet input, @Nonnull final BitSet trueRequired) {
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
