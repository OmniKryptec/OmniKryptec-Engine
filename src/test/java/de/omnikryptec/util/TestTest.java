/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import de.codemakers.base.logger.Logger;
import de.omnikryptec.util.math.Mathf;

public class TestTest {

    public static final void main(String[] args) throws Exception {
	int count = 0;
	long time = System.nanoTime();
	for (float f = 0; f < 100; f += 0.25f) {
	    Math.sqrt(f);
	    //Mathf.sqrtNewton(f, 0.01f);
	    count++;
	}
	long time2 = System.nanoTime();
	System.out.println((time2 - time) / count + "ns");
    }

}
