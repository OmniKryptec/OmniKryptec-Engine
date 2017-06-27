package omnikryptec.particles;

import java.util.ArrayList;
import java.util.Arrays;
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
			if(o1.getDistance()>o2.getDistance()){
				return 1;
			}else if(o1.getDistance()<o2.getDistance()){
				return -1;
			}
			return 0;
		}
	};
	
	
	private static Particle item1;
	private static int attemptPos,i;
	private static float dist;
	/**
	 * Sorts a list of particles so that the particles with the highest distance
	 * from the camera are first, and the particles with the shortest distance
	 * are last.
	 * 
	 * @param list
	 *            - the list of particles needing sorting.
	 */
	@Deprecated
	public static void sortHighToLow(List<Particle> list) {
		for (i = 1; i < list.size(); i++) {
			item1 = list.get(i);
			if ((dist=item1.getDistance()) > list.get(i - 1).getDistance()) {
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
