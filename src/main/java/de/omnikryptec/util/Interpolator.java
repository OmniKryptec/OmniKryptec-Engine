package de.omnikryptec.util;

import de.omnikryptec.util.math.Mathd;
import de.omnikryptec.util.math.Mathf;

public enum Interpolator {
    None {
        @Override
        public double interpolate(double t) {
            return 0.0;
        }
        
        @Override
        public float interpolate(float t) {
            return 0.0F;
        }
    },
    Linear {
        @Override
        public double interpolate(double t) {
            return t;
        }
        
        @Override
        public float interpolate(float t) {
            return t;
        }
    },
    Cubic {
        @Override
        public double interpolate(double t) {
            return (t * t * (3.0 - 2.0 * t));
        }
        
        @Override
        public float interpolate(float t) {
            return (t * t * (3.0F - 2.0F * t));
        }
    },
    Quintic {
        @Override
        public double interpolate(double t) {
            return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
        }
        
        @Override
        public float interpolate(float t) {
            return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
        }
    },
    Cos {
        @Override
        public double interpolate(double t) {
            return (1.0 - Mathd.cos(t * Mathd.PI)) * 0.5;
        }
        
        @Override
        public float interpolate(float t) {
            return (1.0F - Mathf.cos(t * Mathf.PI)) * 0.5F;
        }
    };
    
    public double interpolate(final double t) {
        throw new AbstractMethodError();
    }
    
    public float interpolate(final float t) {
        throw new AbstractMethodError();
    }
    
}
