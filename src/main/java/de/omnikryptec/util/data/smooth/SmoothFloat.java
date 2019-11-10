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

package de.omnikryptec.util.data.smooth;

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.util.updater.Time;

public class SmoothFloat implements IUpdatable {

    private float agility;
    private float target;
    private float actual;

    public SmoothFloat(final float initialValue, final float agility) {
        this.target = initialValue;
        this.actual = initialValue;
        this.agility = agility;
    }

    @Override
    public void update(final Time time) {
        this.actual += (this.target - this.actual) * time.deltaf * this.agility;
    }

    public void instantIncrease(final float increase) {
        this.actual += increase;
        this.target = this.actual;
    }

    public float get() {
        return this.actual;
    }

    public float getTarget() {
        return this.target;
    }

    public void setTarget(final float target) {
        this.target = target;
    }

    public void increaseTarget(final float deltaTarget) {
        this.target += deltaTarget;
    }

    public void setValue(final float f) {
        this.actual = f;
    }

    public float getAgility() {
        return this.agility;
    }

    public void setAgility(final float a) {
        this.agility = a;
    }

    public void setValueAndTarget(final float f) {
        setTarget(f);
        setValue(f);
    }

    @Override
    public String toString() {
        return "Value: " + this.actual + " Target: " + this.target + " Agility: " + this.agility;
    }

}
