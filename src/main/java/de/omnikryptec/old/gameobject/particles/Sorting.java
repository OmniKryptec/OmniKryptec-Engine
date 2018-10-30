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

package de.omnikryptec.old.gameobject.particles;

import java.util.Comparator;
import java.util.List;

/**
 * A simple implementation of an insertion sort. I implemented this very quickly
 * the other day so it may not be perfect or the most efficient! Feel free to
 * implement your own sorter instead.
 *
 * @author Karl
 *
 */
public class Sorting {

    static final Comparator<Particle> PARTICLE_COMPARATOR = new Comparator<Particle>() {

	@Override
	public int compare(Particle o1, Particle o2) {
	    if (o1.getDistance() > o2.getDistance()) {
		return 1;
	    } else if (o1.getDistance() < o2.getDistance()) {
		return -1;
	    }
	    return 0;
	}
    };

    private static Particle item1;
    private static int attemptPos, i;
    private static float dist;

    /**
     * Sorts a list of particles so that the particles with the highest distance
     * from the camera are first, and the particles with the shortest distance are
     * last.
     *
     * @param list - the list of particles needing sorting.
     */
    @Deprecated
    public static void sortHighToLow(List<Particle> list) {
	for (i = 1; i < list.size(); i++) {
	    item1 = list.get(i);
	    if ((dist = item1.getDistance()) > list.get(i - 1).getDistance()) {
		attemptPos = i - 1;
		while (attemptPos != 0 && list.get(attemptPos - 1).getDistance() < dist) {
		    attemptPos--;
		}
		list.remove(i);
		list.add(attemptPos, item1);
	    }
	}
    }

}
