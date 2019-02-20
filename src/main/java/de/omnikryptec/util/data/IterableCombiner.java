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

import java.util.Iterator;

public class IterableCombiner<T> implements Iterable<T> {

    private final Iterable<T>[] iterables;

    public IterableCombiner(final Iterable<T>... iterables) {
        this.iterables = iterables;
    }

    @Override
    public Iterator<T> iterator() {
        return new CombinedIterator<>(this.iterables);
    }

    private static class CombinedIterator<E> implements Iterator<E> {

        private final Iterator<E>[] iterators;
        private int itIndex = 0;

        private CombinedIterator(final Iterable<E>[] iterables) {
            this.iterators = new Iterator[iterables.length];
            for (int i = 0; i < iterables.length; i++) {
                this.iterators[i] = iterables[i].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            return this.iterators.length != 0 && (this.itIndex != this.iterators.length - 1 || this.iterators[this.itIndex].hasNext());
        }

        @Override
        public E next() {
            while (!this.iterators[this.itIndex].hasNext()) {
                this.itIndex++;
            }
            return this.iterators[this.itIndex].next();
        }

        @Override
        public void remove() {
            this.iterators[this.itIndex].remove();
        }

    }

}
