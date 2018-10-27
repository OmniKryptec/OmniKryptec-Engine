package de.omnikryptec.util;

import de.omnikryptec.util.math.Mathf;

public class ARandomTest {

	public static void main(String[] args) {
		System.out.println(Mathf.ceil(-1.5f));
		int ops = 0;
		long time = System.nanoTime();
		for (float f = -100.0f; f <= 100.0f; f += 0.0125f) {
			Math.floor(f);
			ops++;
		}
		long time2 = System.nanoTime();
		System.out.println("Time per op: " + ((time2 - time) / ops) + "ns");
	}

}
