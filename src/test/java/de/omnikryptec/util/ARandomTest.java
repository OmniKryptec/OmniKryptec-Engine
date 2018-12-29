package de.omnikryptec.util;

import java.util.List;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser;
import de.omnikryptec.graphics.shader.base.parser.ShaderSource;

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
        final ShaderParser parser = new ShaderParser();
        parser.addProvider("spacko", "kekekekeke");
        parser.parse("dd", "$define module kek$ $header$ w $header$ bonobo $spacko$");
        parser.parse("kek", "$define shader VERTEX$ $header$ v $header$ mega  $module kek$");
        final List<ShaderSource> src = parser.process();
        for (final ShaderSource s : src) {
            System.out.println(s.getSource());
            System.out.println();
        }
    }

}
