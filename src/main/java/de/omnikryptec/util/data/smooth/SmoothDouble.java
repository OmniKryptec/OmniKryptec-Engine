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

package de.omnikryptec.util.data.smooth;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.util.updater.Time;

public class SmoothDouble implements Updateable {
    
    private double agility;
    private double target;
    private double actual;
    
    public SmoothDouble(double initialValue, double agility) {
        this.target = initialValue;
        this.actual = initialValue;
        this.agility = agility;
    }
    
    @Override
    public void update(Time time) {
        actual += (target - actual) * time.delta * agility;
    }
    
    public void instantIncrease(double increase) {
        this.actual += increase;
        this.target = actual;
    }
    
    public double get() {
        return actual;
    }
    
    public double getTarget() {
        return target;
    }
    
    public void setTarget(double target) {
        this.target = target;
    }
    
    public void increaseTarget(double deltaTarget) {
        this.target += deltaTarget;
    }
    
    public void setValue(double f) {
        actual = f;
    }
    
    public double getAgility() {
        return agility;
    }
    
    public void setAgility(double a) {
        this.agility = a;
    }
    
    public void setValueAndTarget(double f) {
        setTarget(f);
        setValue(f);
    }
    
    @Override
    public String toString() {
        return "Value: " + actual + " Target: " + target + " Agility: " + agility;
    }
    
}
