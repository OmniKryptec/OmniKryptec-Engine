package de.omnikryptec.graphics.display;

public class Smoother {
	private double[] smoothed;
	private int pointer;

	public Smoother() {
		this(300);
	}

	public Smoother(int size) {
		setSmoothingSize(size);
	}

	public void push(double d) {
		smoothed[pointer] = d;
		pointer++;
		pointer %= smoothed.length;
	}

	public double get() {
		double del = 0;
		for (int i = 0; i < smoothed.length; i++) {
			del += smoothed[i];
		}
		return del / smoothed.length;
	}

	public void setSmoothingSize(int i) {
		double[] array = new double[i];
		System.arraycopy(smoothed, 0, array, 0, Math.min(smoothed.length, array.length));
		pointer = smoothed.length;
		pointer %= array.length;
		smoothed = array;
	}

	public long getInverse() {
		return Math.round(1.0 / (get()));
	}
}
