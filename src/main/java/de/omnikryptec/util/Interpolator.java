package de.omnikryptec.util;

public enum Interpolator {
    None, Linear, Cubic, Quintic;
    
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
        default:
            throw new IllegalStateException();
        }
    }
}
