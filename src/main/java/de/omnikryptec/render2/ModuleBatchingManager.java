/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.render2;

import java.nio.FloatBuffer;
import de.omnikryptec.render.batch.module.Module;
import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;

//Helper class for BatchedShader2D
public class ModuleBatchingManager {
    
    private static final QuadSide[] ARRANGED = { QuadSide.TopLeft, QuadSide.TopRight, QuadSide.BotLeft,
            QuadSide.TopRight, QuadSide.BotRight, QuadSide.BotLeft };
    private static final QuadSide[] SIDES = QuadSide.values();
    
    private final float[] global;
    private final float[][] local = new float[SIDES.length][];
    
    public ModuleBatchingManager(int globalCount, int sideCount) {
        this.global = new float[globalCount];
        for (int i = 0; i < this.local.length; i++) {
            this.local[i] = new float[sideCount];
        }
    }
    
    public void put(FloatBuffer b, Module... modules) {
        int globalindex = 0;
        int localindex = 0;
        for (final Module m : modules) {
            if (m.sideIndependant()) {
                if (m.changed()) {
                    m.visit(this.global, null, globalindex);
                }
                globalindex += m.size();
            } else {
                if (m.changed()) {
                    for (int i = 0; i < SIDES.length; i++) {
                        m.visit(this.local[i], SIDES[i], localindex);
                    }
                }
                localindex += m.size();
            }
        }
        for (final QuadSide q : ARRANGED) {
            b.put(global, 0, this.global.length);
            b.put(this.local[q.ordinal()], 0, this.local[q.ordinal()].length);
        }
    }
    
}
