package de.omnikryptec.util.math;

import org.joml.Math;

public strictfp class Mathf {

	/**
	 * All float values above or equal to this value are integer numbers, all float values
	 * below or equal to (-1) * this value are integer numbers.
	 */
	private static final float TWO_POW_23 = 8388608.0f;

	public static float clamp(float in, float min, float max) {
		return in < min ? min : (in > max ? max : in);
	}

	public static float clamp01(float in) {
		return in < 0.0f ? 0.0f : (in > 1.0f ? 1.0f : in);
	}

	public static float sin(float rad) {
		return (float) Math.sin(rad);
	}

	public static float cos(float rad) {
		return (float) Math.cos(rad);
	}

	public static float tan(float rad) {
		return (float) Math.tan(rad);
	}

	public static float arcsin(float x) {
		return (float) Math.asin(x);
	}

	public static float arccos(float x) {
		return (float) Math.acos(x);
	}

	public static float arctan(float x) {
		return (float) java.lang.Math.atan(x);
	}

	public static float arctan2(float y, float x) {
		return (float) Math.atan2(y, x);
	}

	public static float sqrt(float value) {
		return (float) Math.sqrt(value);
	}

	public static float abs(float value) {
		return value < 0.0f ? 0.0f - value : value;
	}

	public static float min(float v0, float v1) {
		return v0 < v1 ? v0 : v1;
	}

	public static float max(float v0, float v1) {
		return v0 > v1 ? v0 : v1;
	}

	public static float floor(float value) {
		if (value != value) {
			// NaN
			return value;
		}
		if (value >= TWO_POW_23 || value <= -TWO_POW_23) {
			return value;
		}
		int intvalue = (int) value;
		if (value < 0 && intvalue != value) {
			intvalue--;
		}
		return intvalue;
	}

	public static float ceil(float value) {
		if (value != value) {
			// NaN
			return value;
		}
		if (value >= TWO_POW_23 || value <= -TWO_POW_23) {
			return value;
		}
		int intvalue = (int) value;
		if (value > 0 && intvalue != value) {
			intvalue++;
		}
		return intvalue;
	}
}
