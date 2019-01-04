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

import java.util.Objects;

public class DynamicArray<E> {
    
    private Object[] array;
    
    public DynamicArray() {
        this(10);
    }
    
    public DynamicArray(final int initialSize) {
        this.array = new Object[initialSize];
    }
    
    public void set(final int index, final E e) {
        if (index >= this.array.length) {
            grow(index - array.length + 1);
        }
        this.array[index] = e;
    }
    
    public E get(final int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        return (E) this.array[index];
    }
    
    private void grow(int amount) {
        final Object[] newArray = new Object[size() + amount];
        System.arraycopy(this.array, 0, newArray, 0, this.array.length);
        this.array = newArray;
    }
    
    public int size() {
        return this.array.length;
    }
    
    public void trim() {
        int index = array.length - 1;
        while (array[index] == null) {
            index--;
        }
        if (index < array.length - 1) {
            Object[] newArray = new Object[index + 1];
            System.arraycopy(array, 0, newArray, 0, newArray.length);
            this.array = newArray;
        }
    }
    
    public boolean contains(Object object) {
        for (Object i : array) {
            if (Objects.equals(i, object)) {
                return true;
            }
        }
        return false;
    }
    
    public int indexOf(Object object) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], object)) {
                return i;
            }
        }
        return -1;
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
