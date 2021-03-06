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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class DynamicArray<E> implements Iterable<E> {
    
    private Object[] array;
    
    public DynamicArray() {
        this(0);
    }
    
    public DynamicArray(final int initialSize) {
        this.array = new Object[initialSize];
    }
    
    public void add(E e) {
        set(size(), e);
    }
    
    public void set(final int index, final E e) {
        if (index >= this.array.length) {
            grow(index - this.array.length + 1);
        }
        this.array[index] = e;
    }
    
    public E get(final int index) {
        if (index < 0 || index >= size()) {
            return null;//throw new IndexOutOfBoundsException("" + index);
        }
        return (E) this.array[index];
    }
    
    public int size() {
        return this.array.length;
    }
    
    public void trimEnd() {
        grow(-nulls(true));
    }
    
    public void trimNulls() {
        int nonnullIndex = 0;
        for (int i = 0; i < this.array.length; i++) {
            if (this.array[i] != null) {
                this.array[nonnullIndex++] = this.array[i];
                if (i > nonnullIndex - 1) {
                    this.array[i] = null;
                }
            }
        }
        grow(-this.array.length + nonnullIndex);
    }
    
    public void append(final DynamicArray<E> other) {
        appendUnsafe(other.array);
    }
    
    public void append(final E[] other) {
        appendUnsafe(other);
    }
    
    public boolean contains(final Object object) {
        for (final Object i : this.array) {
            if (Objects.equals(i, object)) {
                return true;
            }
        }
        return false;
    }
    
    public int indexOf(final Object object) {
        for (int i = 0; i < this.array.length; i++) {
            if (Objects.equals(this.array[i], object)) {
                return i;
            }
        }
        return -1;
    }
    
    public void clear(final boolean nogarbage) {
        if (nogarbage) {
            for (int i = 0; i < this.array.length; i++) {
                this.array[i] = null;
            }
        } else {
            this.array = new Object[this.array.length];
        }
    }
    
    public Object[] arrayAccess() {
        return this.array;
    }
    
    private void grow(final int amount) {
        final Object[] newArray = new Object[size() + amount];
        System.arraycopy(this.array, 0, newArray, 0, Math.min(this.array.length, newArray.length));
        this.array = newArray;
    }
    
    private void appendUnsafe(final Object[] other) {
        final int newsize = this.size() + other.length;
        final Object[] newarray = new Object[newsize];
        System.arraycopy(this.array, 0, newarray, 0, this.array.length);
        System.arraycopy(other, 0, newarray, this.array.length, other.length);
        this.array = newarray;
    }
    
    private int nulls(final boolean end) {
        int i = 0;
        while (this.array[end ? this.array.length - 1 - i : i] == null) {
            i++;
        }
        return i;
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
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.array);
    }
    
    @Override
    public String toString() {
        return Arrays.toString(this.array);
    }
    
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }
    
    private class Itr implements Iterator<E> {
        
        private int index = 0;
        
        @Override
        public boolean hasNext() {
            return this.index < DynamicArray.this.array.length;
        }
        
        @Override
        public E next() {
            return (E) DynamicArray.this.array[this.index++];
        }
        
        //Remove not supported
    }
}
