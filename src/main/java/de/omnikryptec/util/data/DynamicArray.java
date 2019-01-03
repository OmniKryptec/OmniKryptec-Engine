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

package de.omnikryptec.util.data;

public class DynamicArray<E> {
    
    private Object[] array;
    private int grow;
    
    public DynamicArray() {
        this(10, 2);
    }
    
    public DynamicArray(final int initialSize, final int grow) {
        this.array = new Object[grow];
        this.grow = grow;
    }
    
    public void set(final int index, final E e) {
        if (index >= this.array.length) {
            grow();
        }
        this.array[index] = e;
    }
    
    public E get(final int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        return (E) this.array[index];
    }
    
    private void grow() {
        final Object[] newArray = new Object[size() + this.grow];
        System.arraycopy(this.array, 0, newArray, 0, this.array.length);
        this.array = newArray;
    }
    
    public int size() {
        return this.array.length;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof DynamicArray) {
            final DynamicArray<?> other = (DynamicArray<?>) obj;
            if (other.size() != this.size()) {
                return false;
            }
            for (int i = 0; i < size(); i++) {
                if (!other.get(i).equals(get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
