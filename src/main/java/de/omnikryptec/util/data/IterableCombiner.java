package de.omnikryptec.util.data;

import java.util.Iterator;

public class IterableCombiner<T> implements Iterable<T> {
    
    private Iterable<T>[] iterables;
    
    public IterableCombiner(Iterable<T>... iterables) {
        this.iterables = iterables;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new CombinedIterator<T>(iterables);
    }
    
    private static class CombinedIterator<E> implements Iterator<E> {
        
        private Iterator<E>[] iterators;
        private int itIndex = 0;
        
        private CombinedIterator(Iterable<E>[] iterables) {
            this.iterators = new Iterator[iterables.length];
            for (int i = 0; i < iterables.length; i++) {
                iterators[i] = iterables[i].iterator();
            }
        }
        
        @Override
        public boolean hasNext() {
            return iterators.length == 0 ? false
                    : (itIndex == iterators.length - 1 ? iterators[itIndex].hasNext() : true);
        }
        
        @Override
        public E next() {
            while (!iterators[itIndex].hasNext()) {
                itIndex++;
            }
            return iterators[itIndex].next();
        }
        
        @Override
        public void remove() {
            iterators[itIndex].remove();
        }
        
    }
    
}
