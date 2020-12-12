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

package de.omnikryptec.util.profiling;

import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.math.Mathd;

public class ProfileHelper {
    
    public static final byte BIT_AVG = 1 << 0;
    public static final byte BIT_SUM = 1 << 1;
    public static final byte BIT_MIN = 1 << 2;
    public static final byte BIT_MAX = 1 << 3;
    
    private static final byte ALL_BITS = BIT_AVG | BIT_SUM | BIT_MAX | BIT_MIN;
    
    private long sum = 0;
    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;
    
    public void push(long value) {
        this.sum += value;
        this.min = Math.min(value, this.min);
        this.max = Math.max(value, this.max);
    }
    
    public long getSum() {
        return this.sum;
    }
    
    public long getMin() {
        return this.min;
    }
    
    public long getMax() {
        return this.max;
    }
    
    public double getAvg(long count, int eff) {
        return Mathd.round(this.sum / (double) count, eff);
    }
    
    public void append(String name, long count, int eff, StringBuilder builder) {
        append(name, count, eff, "", ALL_BITS, builder);
    }
    
    public void append(String name, long count, int eff, String measure, StringBuilder builder) {
        append(name, count, eff, measure, ALL_BITS, builder);
    }
    
    public void append(String name, long count, int eff, byte opt, StringBuilder builder) {
        append(name, count, eff, "", opt, builder);
    }
    
    public void append(String name, long count, int eff, String measure, byte options, StringBuilder builder) {
        if (MathUtil.containsBit(options, BIT_AVG)) {
            builder.append(name).append(" avg: ").append(getAvg(count, eff)).append(measure).append('\n');
        }
        if (MathUtil.containsBit(options, BIT_MIN)) {
            builder.append(name).append(" min: ").append(getMin()).append(measure).append('\n');
        }
        if (MathUtil.containsBit(options, BIT_MAX)) {
            builder.append(name).append(" max: ").append(getMax()).append(measure).append('\n');
        }
        if (MathUtil.containsBit(options, BIT_SUM)) {
            builder.append(name).append(" sum: ").append(getSum()).append(measure).append('\n');
        }
    }
}
