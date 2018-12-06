package de.omnikryptec.util;

import java.util.Random;

import de.omnikryptec.util.math.MathUtil;

public class ARandomTest {

    public static void main(final String[] args) {
        /*
         * System.out.println(Math.rint(-1.5)); System.out.println(Mathf.ceil(-1.5f));
         * int ops = 0; long time = System.nanoTime(); for (float f = -100.0f; f <=
         * 100.0f; f += 0.0125f) { Mathf.rint(f); ops++; } long time2 =
         * System.nanoTime(); System.out.println("Time per op: " + ((time2 - time) /
         * ops) + "ns"); for (double f = -100.0; f <= 100.0; f += 0.0125) {
         * if(Mathd.rint(f)!=Math.rint(f)) { System.out.println(f); } }
         */
        final Integer[] somearray = new Integer[] { 100, 50 };
        final int[] weights = new int[] { 2, 1 };
        int a = 0, b = 0;
        final Random random = new Random();
        for (int i = 0; i < 100000000; i++) {
            final int s = MathUtil.getWeightedRandom(random, somearray, weights);
            if (s == 100) {
                a++;
            } else {
                b++;
            }
        }
        System.out.println("a: " + a);
        System.out.println("b: " + b);
        System.out.println("/: " + a / (double) (a + b));
    }

}
