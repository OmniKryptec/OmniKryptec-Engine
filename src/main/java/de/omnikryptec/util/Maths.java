package de.omnikryptec.util;

public class Maths {

	public static int[] calculateViewport(double aspectRatio, int w, int h) {
		int[] viewport = new int[4];
		viewport[0] = 0;
		viewport[1] = 0;
		viewport[2] = w;
		viewport[3] = h;
		if (aspectRatio > 0) {
			if ((double) w / (double) h <= aspectRatio) {
				viewport[3] = (int) (w * (1.0 / aspectRatio));
				viewport[1] = (int) ((h - viewport[3]) * 0.5);
			} else {
				viewport[2] = (int) (h * aspectRatio);
				viewport[0] = (int) ((w - viewport[2]) * 0.5);
			}
		}
		return viewport;
	}
	
}
