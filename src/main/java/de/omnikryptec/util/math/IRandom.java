package de.omnikryptec.util.math;

import de.omnikryptec.util.UnsupportedOperationException;

public interface IRandom {

	int nextInt(int n);
	float nextFloat();
	
	double nextDouble() throws UnsupportedOperationException;
	long nextLong() throws UnsupportedOperationException;
	
	default int nextInt() {
		return nextInt(Integer.MAX_VALUE);
	}
}
