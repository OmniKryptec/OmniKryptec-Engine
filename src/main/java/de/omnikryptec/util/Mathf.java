package de.omnikryptec.util;

public class Mathf {

	public static float clamp(float in, float min, float max) {
		return in < min ? min : (in > max ? max : in);
	}

	public static float clamp01(float in) {
		return in < 0.0f ? 0.0f : (in > 1.0f ? 1.0f : in);
	}

}
