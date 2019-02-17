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
            return this.iterators.length == 0 ? false
                    : (this.itIndex == this.iterators.length - 1 ? this.iterators[this.itIndex].hasNext() : true);
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
