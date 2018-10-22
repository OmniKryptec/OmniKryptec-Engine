package de.omnikryptec.util;

public class Maths {

	/**
	 * Creates a viewport with a particular aspect ratio and the given width and
	 * height. The viewport will be centered and of maximum size for the given
	 * parameters. That means that either the final width or the final height might
	 * be smaller than specified to maintain the given aspect ratio.
	 * 
	 * @param aspectRatio the aspect ratio
	 * @param w           the maximum width
	 * @param h           the maximum height
	 * @return an array consisting of x1, y1, x2, y2; in that particular order
	 */
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
