package de.omnikryptec.util;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.util.data.FixedStack;
import de.omnikryptec.util.math.Mathd;

public class Profiler {
    //TODO improve min and max stuff
    
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
    
    public static void clear() {
        map.clear();
        h.clear();
    }
    
    private static Struct get(String id) {
        Struct s = map.get(id);
        if (s == null) {
            s = new Struct();
            map.put(id, s);
        }
        return s;
    }
    
    public static void begin(String id) {
        Struct s = get(id);
        if (s.open) {
            throw new IllegalStateException("never ended profiling with ID " + id);
        }
        s.count++;
        s.open = true;
        h.push(id);
        long time = System.nanoTime();
        s.sum += -time;
        s.beginTimeTmp = time;
    }
    
    //TODO insert specific data (vertex count, flush count, etc)
    public static void end() {
        long time = System.nanoTime();
        String id = h.pop();
        Struct s = get(id);
        if (!s.open) {
            throw new IllegalStateException("never started profiling with ID " + id);
        }
        long dif = time - s.beginTimeTmp;
        s.sum += time;
        s.max = Math.max(dif, s.max);
        s.min = Math.min(dif, s.min);
        s.open = false;
    }
    
    public static String currentInfo() {
        if (map.isEmpty()) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (String id : map.keySet()) {
            Struct s = map.get(id);
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
        }
        b.append("-------------------");
        return b.toString();
    }
    
}
