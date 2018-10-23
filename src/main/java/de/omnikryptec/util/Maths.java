package de.omnikryptec.util;

import java.util.Random;

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
	
	public static <T> T getWeightedRandom(Random random, T[] ts, int[] weights) {
        int sum = 0;
        for (int i : weights) {
            sum += i;
        }
        int rand = random.nextInt(sum - 1) + 1;
        for (int i = 0; i < ts.length; i++) {
            rand -= weights[i];
            if (rand <= 0) {
                return ts[i];
            }
        }
        return ts[0];
    }
	
	public static int toPowerOfTwo(int n) {
        return 1 << (32 - Integer.numberOfLeadingZeros(n - 1));
    }

    public static boolean isPowerOfTwo(int n) {
        return (n & -n) == n;
    }

}
