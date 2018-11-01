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

public class SmoothFloat implements Updateable {
    
    private float agility;
    private float target;
    private float actual;
    
    public SmoothFloat(float initialValue, float agility) {
        this.target = initialValue;
        this.actual = initialValue;
        this.agility = agility;
    }
    
    @Override
    public void update(float deltaTime) {
        actual += (target - actual) * deltaTime * agility;
    }
    
    public void instantIncrease(float increase) {
        this.actual += increase;
        this.target = actual;
    }
    
    public float get() {
        return actual;
    }
    
    public float getTarget() {
        return target;
    }
    
    public void setTarget(float target) {
        this.target = target;
    }
    
    public void increaseTarget(float deltaTarget) {
        this.target += deltaTarget;
    }
    
    public void setValue(float f) {
        actual = f;
    }
    
    public float getAgility() {
        return agility;
    }
    
    public void setAgility(float a) {
        this.agility = a;
    }
    
    public void setValueAndTarget(float f) {
        setTarget(f);
        setValue(f);
    }
    
    @Override
    public String toString() {
        return "Value: " + actual + " Target: " + target + " Agility: " + agility;
    }
    
}
