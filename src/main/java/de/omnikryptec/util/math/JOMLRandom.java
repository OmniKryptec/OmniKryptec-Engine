package de.omnikryptec.util.math;

import org.joml.Random;

import de.omnikryptec.util.UnsupportedOperationException;

public class JOMLRandom implements IRandom{
	
	private final Random jomlRandom;
	
	public JOMLRandom(long seed) {
		this.jomlRandom = new Random(seed);
	}
	
	@Override
	public int nextInt(int n) {
		return jomlRandom.nextInt(n);
	}

	@Override
	public float nextFloat() {
		return jomlRandom.nextFloat();
	}

	@Override
	public double nextDouble() {
		throw new UnsupportedOperationException("nextDouble");
	}

	@Override
	public long nextLong() {
		throw new UnsupportedOperationException("nextLong");
	}

	public Random getRandom() {
		return jomlRandom;
	}
	
}
