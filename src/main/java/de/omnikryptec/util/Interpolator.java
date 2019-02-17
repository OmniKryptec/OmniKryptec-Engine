package de.omnikryptec.util;

import de.omnikryptec.util.math.Mathd;
import de.omnikryptec.util.math.Mathf;

public enum Interpolator {
    None, Linear, Cubic, Quintic, Cos;
    
    public double interpolate(final double t) {
        switch (this) {
        case None:
            return 0;
        case Linear:
            return t;
        case Cubic:
            return (t * t * (3 - 2 * t));
        case Quintic:
            return t * t * t * (t * (t * 6 - 15) + 10);
        case Cos:
            return (1 - Mathd.cos(t * Mathd.PI)) * 0.5;
        default:
            throw new IllegalStateException();
        }
    }
    
    public float interpolate(final float t) {
        switch (this) {
        case None:
            return 0;
        case Linear:
            return t;
        case Cubic:
            return (t * t * (3 - 2 * t));
        case Quintic:
            return t * t * t * (t * (t * 6 - 15) + 10);
        case Cos:
            return (1 - Mathf.cos(t * Mathf.PI)) * 0.5f;
        default:
            throw new IllegalStateException();
        }
    }
}
