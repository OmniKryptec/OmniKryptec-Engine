/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.util.math;

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
