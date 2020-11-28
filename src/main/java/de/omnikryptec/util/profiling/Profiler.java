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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.data.FixedStack;
import de.omnikryptec.util.math.Mathd;

//TODO profile the current status of stuff? print FPS information?
public class Profiler {
    
    private static class Struct {
        long sum;
        long count;
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        boolean open;
        
        List<Double> l;
        long beginTimeTmp;
    }
    
    private static final Map<Object, Struct> map = new HashMap<>();
    private static final FixedStack<Object> h = new FixedStack<>(20);
    private static final Map<Object, IProfiler> additional = new HashMap<>();
    private static boolean enabled = false;
    private static boolean graph = false;
    
    public static void setGraphEnabled(boolean b) {
        graph = b;
    }
    
    public static boolean isGraphEnabled() {
        return graph;
    }
    
    public static void setEnabled(final boolean b) {
        enabled = b;
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static void clear() {
        map.clear();
        h.clear();
    }
    
    public static void addIProfiler(Object id, IProfiler profiler) {
        Util.ensureNonNull(id);
        Util.ensureNonNull(profiler);
        additional.put(id, profiler);
    }
    
    private static Struct get(final Object id) {
        if (!isEnabled()) {
            return null;
        }
        Struct s = map.get(id);
        if (s == null) {
            s = new Struct();
            if (graph) {
                s.l = new ArrayList<>();
            }
            map.put(id, s);
        }
        return s;
    }
    
    public static void begin(final Object id) {
        if (!isEnabled()) {
            return;
        }
        final Struct s = get(id);
        if (s.open) {
            throw new IllegalStateException("never ended profiling with ID " + id);
        }
        s.count++;
        s.open = true;
        h.push(id);
        final long time = System.nanoTime();
        s.sum += -time;
        s.beginTimeTmp = time;
    }
    
    public static void end(Object... objects) {
        if (!isEnabled()) {
            return;
        }
        final long time = System.nanoTime();
        final Object id = h.pop();
        final Struct s = get(id);
        if (!s.open) {
            throw new IllegalStateException("never started profiling with ID " + id);
        }
        final long dif = time - s.beginTimeTmp;
        if (s.l != null) {
            s.l.add(dif / 1000000d);//this is now in ms
        }
        s.sum += time;
        s.max = Math.max(dif, s.max);
        s.min = Math.min(dif, s.min);
        s.open = false;
        if (additional.get(id) != null) {
            additional.get(id).dealWith(dif, objects);
        }
    }
    
    public static String currentInfo() {
        if (map.isEmpty()) {
            return "Nothing has been profiled";
        }
        final StringBuilder b = new StringBuilder();
        for (final Object id : map.keySet()) {
            final Struct s = map.get(id);
            if (s.open) {
                throw new IllegalStateException("the profiling with ID " + id + " is still open");
            }
            b.append("-------------------\n");
            b.append("ID: " + id).append('\n');
            b.append("Call-count: " + s.count).append('\n');
            b.append("Time avg: " + Mathd.round((s.sum / (double) s.count) * 1e-6, 3) + "ms").append('\n');
            b.append("Time min: " + Mathd.round((s.min) * 1e-6, 3) + "ms").append('\n');
            b.append("Time max: " + Mathd.round((s.max) * 1e-6, 3) + "ms").append('\n');
            b.append("Time sum: " + Mathd.round(s.sum * 1e-9, 5) + "s").append('\n');
            if (s.l != null) {
                b.append(ConsoleGraphing.graph(120, 15, s.l, "Time-graph", "profile cycle index", "ms"));
            }
            if (additional.get(id) != null) {
                additional.get(id).writeData(b, s.count);
            }
        }
        b.append("-------------------");
        return b.toString();
    }
    
}
