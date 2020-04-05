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

package de.omnikryptec.util.data;

public class Smoother {

    private double[] smoothed;
    private int pointer;

    public Smoother() {
        this(300);
    }

    public Smoother(final int size) {
        setSmoothingSize(size);
    }

    public void push(final double d) {
        this.smoothed[this.pointer] = d;
        this.pointer++;
        this.pointer %= this.smoothed.length;
    }

    public double get() {
        double del = 0;
        for (int i = 0; i < this.smoothed.length; i++) {
            del += this.smoothed[i];
        }
        return del / this.smoothed.length;
    }

    public void setSmoothingSize(final int i) {
        final double[] array = new double[i];
        if (this.smoothed != null) {
            System.arraycopy(this.smoothed, 0, array, 0, Math.min(this.smoothed.length, array.length));
            this.pointer = this.smoothed.length;
            this.pointer %= array.length;
        }
        this.smoothed = array;
    }

    public long getInverse() {
        return Math.round(1.0 / (get()));
    }
}
