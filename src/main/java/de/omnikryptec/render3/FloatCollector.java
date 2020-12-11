package de.omnikryptec.render3;

import org.joml.Matrix3x2fc;

public interface FloatCollector {
    
    FloatCollector put(Matrix3x2fc mat);
    
    FloatCollector put(float f);
    
    default FloatCollector put(float[] a) {
        return put(a, 0, a.length);
    }
    
    FloatCollector put(float[] a, int offset, int length);
    
    int remaining();
    
    int position();
}
