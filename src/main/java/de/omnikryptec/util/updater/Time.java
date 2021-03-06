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

package de.omnikryptec.util.updater;

import java.util.function.UnaryOperator;

public class Time {
    
    public final long opCount;
    public final long ops;
    
    public final double current;
    public final double delta;
    
    public final float currentf;
    public final float deltaf;
    
    public Time(final long opCount, final long ops, final double current, final double delta) {
        this.opCount = opCount;
        this.ops = ops;
        this.current = current;
        this.delta = delta;
        this.currentf = (float) current;
        this.deltaf = (float) delta;
    }
    
    public Time transformed(UnaryOperator<Time> function) {
        return function.apply(this);
    }
    
    @Override
    public String toString() {
        return "Time [current=" + this.current + " delta=" + this.delta + " opCount=" + this.opCount + " OPS="
                + this.ops + "]";
    }
    
}
