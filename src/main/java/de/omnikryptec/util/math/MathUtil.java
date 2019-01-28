/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.util.math;

import java.util.Random;

public class MathUtil {

    /**
     * Creates a viewport with a particular aspect ratio and the given width and
     * height. The viewport will be centered and of maximum size for the given
     * parameters. That means that either the final width or the final height might
     * be smaller than specified to maintain the given aspect ratio. <br>
     * Aspect ratios equal to or smaller than 0.0 are ignored and the viewport
     * becomes {0,0,width,height}
     *
     * @param aspectRatio the aspect ratio
     * @param w           the maximum width
     * @param h           the maximum height
     *
     * @return an array consisting of x, y, width, height; in that particular order
     */
    public static int[] calculateViewport(final double aspectRatio, final int w, final int h) {
        final int[] viewport = new int[4];
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

    public static <T> T getWeightedRandom(final Random random, final T[] ts, final int[] weights) {
        int sum = 0;
        for (final int i : weights) {
            sum += i;
        }
        int rand = random.nextInt(sum) + 1;
        for (int i = 0; i < ts.length; i++) {
            if (rand <= weights[i]) {
                return ts[i];
            }
            rand -= weights[i];
        }
        return ts[0];
    }

    public static int toPowerOfTwo(final int n) {
        return 1 << (32 - Integer.numberOfLeadingZeros(n - 1));
    }

    public static boolean isPowerOfTwo(final int n) {
        return (n & -n) == n;
    }

}
