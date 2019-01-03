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

package de.omnikryptec.util;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser;
import de.omnikryptec.graphics.shader.base.parser.ShaderSource;

import java.util.List;

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
