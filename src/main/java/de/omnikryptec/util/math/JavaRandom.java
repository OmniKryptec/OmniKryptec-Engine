package de.omnikryptec.util.math;

import java.util.Random;

public class JavaRandom implements IRandom{

	private final Random javaRandom;
	
	public JavaRandom() {
		this.javaRandom = new Random();
	}
	
	public JavaRandom(long seed) {
		this.javaRandom = new Random(seed); 
	}
	
	@Override
	public int nextInt(int n) {
		return javaRandom.nextInt(n);
	}

	@Override
	public float nextFloat() {
		return javaRandom.nextFloat();
	}

	@Override
	public double nextDouble() {
		return javaRandom.nextDouble();
	}

	@Override
	public long nextLong() {
		return javaRandom.nextLong();
	}

	public Random getRandom() {
		return javaRandom;
	}
	
}
