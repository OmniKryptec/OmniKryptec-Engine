package de.omnikryptec.util.math;

import org.joml.Math;

public strictfp class Mathd {

	public static final double PI = java.lang.Math.PI;
	public static final double E = java.lang.Math.E;

	/**
	 * All double values above or equal to this value are integer numbers, all
	 * double values below or equal to (-1) * this value are integer numbers.
	 */
	private static final double TWO_POW_52 = 4503599627370496.0d;

	public static double clamp(double in, double min, double max) {
		return in < min ? min : (in > max ? max : in);
	}

	public static double clamp01(double in) {
		return in < 0.0 ? 0.0 : (in > 1.0 ? 1.0 : in);
	}

	public static double sin(double rad) {
		return Math.sin(rad);
	}

	public static double cos(double rad) {
		return Math.cos(rad);
	}

	public static double tan(double rad) {
		return Math.tan(rad);
	}

	public static double arcsin(double x) {
		return Math.asin(x);
	}

	public static double arccos(double x) {
		return Math.acos(x);
	}

	public static double arctan(double x) {
		return java.lang.Math.atan(x);
	}

	public static double arctan2(double y, double x) {
		return Math.atan2(y, x);
	}

	public static double sqrt(double value) {
		return Math.sqrt(value);
	}

	public static double abs(double value) {
		return value < 0.0 ? 0.0 - value : value;
	}

	public static double min(double v0, double v1) {
		return v0 < v1 ? v0 : v1;
	}

	public static double max(double v0, double v1) {
		return v0 > v1 ? v0 : v1;
	}

	public static double floor(double value) {
		if (value != value) {
			// NaN
			return value;
		}
		if (value >= TWO_POW_52 || value <= -TWO_POW_52) {
			return value;
		}
		long intvalue = (long) value;
		if (value < 0 && intvalue != value) {
			intvalue--;
		}
		return intvalue;
	}

	public static double ceil(double value) {
		if (value != value) {
			// NaN
			return value;
		}
		if (value >= TWO_POW_52 || value <= -TWO_POW_52) {
			return value;
		}
		long intvalue = (long) value;
		if (value > 0 && intvalue != value) {
			intvalue++;
		}
		return intvalue;
	}
	
	public static double rint(double value) {
		if (value != value) {
			// NaN
			return value;
		}
		if (value > 0 && value < TWO_POW_52) {
			return (TWO_POW_52 + value) - TWO_POW_52;
		} else if (value < 0 && value > -TWO_POW_52) {
			return (-TWO_POW_52 + value) + TWO_POW_52;
		}
		return value;
	}
}