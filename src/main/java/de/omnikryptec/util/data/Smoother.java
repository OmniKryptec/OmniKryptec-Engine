/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.util.data;

public class Smoother {
    
    private double[] smoothed;
    private int pointer;
    
    public Smoother() {
        this(300);
    }
    
    public Smoother(int size) {
        setSmoothingSize(size);
    }
    
    public void push(double d) {
        smoothed[pointer] = d;
        pointer++;
        pointer %= smoothed.length;
    }
    
    public double get() {
        double del = 0;
        for (int i = 0; i < smoothed.length; i++) {
            del += smoothed[i];
        }
        return del / smoothed.length;
    }
    
    public void setSmoothingSize(int i) {
        double[] array = new double[i];
        if (smoothed != null) {
            System.arraycopy(smoothed, 0, array, 0, Math.min(smoothed.length, array.length));
            pointer = smoothed.length;
            pointer %= array.length;
        }
        smoothed = array;
    }
    
    public long getInverse() {
        return Math.round(1.0 / (get()));
    }
}
