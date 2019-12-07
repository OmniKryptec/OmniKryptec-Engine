package de.omnikryptec.util;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.util.data.FixedStack;
import de.omnikryptec.util.math.Mathd;

public class Profiler {
    
    private static class Struct {
        long sum;
        long count;
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        boolean open;
        
        long beginTimeTmp;
    }
    
    private static final Map<String, Struct> map = new HashMap<>();
    private static final FixedStack<String> h = new FixedStack<>(20);
    private static final Map<String, IProfiler> additional = new HashMap<>();
    private static boolean enabled = false;
    
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
    
    public static void addIProfiler(String id, IProfiler profiler) {
        Util.ensureNonNull(id);
        Util.ensureNonNull(profiler);
        additional.put(id, profiler);
    }
    
    private static Struct get(final String id) {
        if (!isEnabled()) {
            return null;
        }
        Struct s = map.get(id);
        if (s == null) {
            s = new Struct();
            map.put(id, s);
        }
        return s;
    }
    
    public static void begin(final String id) {
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
        final String id = h.pop();
        final Struct s = get(id);
        if (!s.open) {
            throw new IllegalStateException("never started profiling with ID " + id);
        }
        final long dif = time - s.beginTimeTmp;
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
            return "";
        }
        final StringBuilder b = new StringBuilder();
        for (final String id : map.keySet()) {
            final Struct s = map.get(id);
            if (s.open) {
                throw new IllegalStateException("the profiling with ID " + id + " is still open");
            }
            b.append("-------------------\n");
            b.append("ID: " + id).append('\n');
            b.append("Count: " + s.count).append('\n');
            b.append("Average time: " + Mathd.round((s.sum / (double) s.count) * 1e-6, 3) + "ms").append('\n');
            b.append("Complete time spend: " + Mathd.round(s.sum * 1e-9, 5) + "s").append('\n');
            b.append("Min time: " + Mathd.round((s.min) * 1e-6, 3) + "ms").append('\n');
            b.append("Max time: " + Mathd.round((s.max) * 1e-6, 3) + "ms").append('\n');
            if (additional.get(id) != null) {
                additional.get(id).writeData(b);
            }
        }
        b.append("-------------------");
        return b.toString();
    }
    
}
