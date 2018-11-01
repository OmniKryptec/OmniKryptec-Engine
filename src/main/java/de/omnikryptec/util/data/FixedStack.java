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

/**
 * A data structure based on the FILO-principle with a fixed size.
 *
 * @param <T> the type of objects that can be stored in this {@link FixedStack}
 *
 * @author pcfreak9000
 */
public class FixedStack<T> {
    
    private Object[] array;
    private int index = 0;
    
    /**
     * Creates a new {@link FixedStack}.
     *
     * @param size the size of the new stack.
     */
    public FixedStack(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must ne greater than 0!");
        }
        array = new Object[size];
    }
    
    /**
     * Adds an object on top of this {@link FixedStack}.
     *
     * @param i the object to be added
     */
    public void push(T i) {
        array[index] = i;
        index++;
    }
    
    /**
     * Retrieves and removes the top element of this {@link FixedStack}.
     *
     * @return the top element
     */
    public T pop() {
        index--;
        return (T) array[index];
    }
    
    /**
     * Retrieves, but does not remove, the top element of this {@link FixedStack}.
     *
     * @return the top element
     */
    public T top() {
        return (T) array[index - 1];
    }
    
    /**
     * @return if this {@link FixedStack} is empty
     */
    public boolean isEmpty() {
        return index == 0;
    }
    
    /**
     * @return if this {@link FixedStack} is full
     */
    public boolean isFull() {
        return index == array.length;
    }
    
    /**
     * The amount of objects stored in this {@link FixedStack}.
     *
     * @return used capacity
     */
    public int filled() {
        return index;
    }
    
    /**
     * The total capacity of this {@link FixedStack}.
     *
     * @return total capacity
     */
    public int total() {
        return array.length;
    }
    
    /**
     * Sets all entries of this {@link FixedStack} to null and its index to 0. In
     * other words, this {@link FixedStack} is emptied.
     */
    public void clear() {
        for (int i = 0; i < index; i++) {
            array[i] = null;
        }
        index = 0;
    }
}
