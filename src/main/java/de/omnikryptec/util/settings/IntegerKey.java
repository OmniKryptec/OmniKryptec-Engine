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

package de.omnikryptec.util.settings;

public class IntegerKey implements Defaultable {
    
    private static int next = 0;
    
    public static IntegerKey next(Object object) {
        IntegerKey key = new IntegerKey(next, object);
        next++;
        return key;
    }
    
    private final int value;
    private final Object defObj;
    
    public IntegerKey(final int value) {
        this(value, null);
    }
    
    public IntegerKey(final int value, final Object defObject) {
        this.value = value;
        this.defObj = defObject;
    }
    
    public int get() {
        return this.value;
    }
    
    @Override
    public <T> T getDefault() {
        return (T) this.defObj;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof IntegerKey) {
            if (((IntegerKey) obj).value == this.value) {
                return true;
            }
        }
        return false;
    }
}
