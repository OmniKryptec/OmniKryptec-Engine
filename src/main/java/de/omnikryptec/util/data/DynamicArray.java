package de.omnikryptec.util.data;

import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class DynamicArray<E> extends AbstractList<E> {

	private Object[] array;
	private int grow = 2;
	private int upperBound;
	private int lowerBound;

	public DynamicArray() {
		this(10, 2);
	}

	public DynamicArray(int initialSize, int grow) {
		this.array = new Object[initialSize];
		this.grow = grow;
		this.upperBound = 0;
		this.lowerBound = array.length;
	}

	@Override
	public E get(int index) {
		return (E) array[index];
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, E element) {
		add(index, element, grow);
	}

	public void add(int index, E element, int grow) {
		if (element == null) {
			throw new NullPointerException();
		}
		if (array.length <= index) {
			grow(grow);
		}
		array[index] = element;
		upperBound = Math.max(index, upperBound);
		lowerBound = Math.min(index, lowerBound);
	}

	@Override
	public E set(int index, E element) {
		if (element == null) {
			return remove(index);
		} else {
			if (array.length <= index) {
				grow(grow);
			}
			Object old = array[index];
			array[index] = element;
			upperBound = Math.max(index, upperBound);
			lowerBound = Math.min(index, lowerBound);
			return (E) old;
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean modified = false;
		for (E e : c) {
			add(index++, e, c.size() - array.length + index);
			modified = true;
		}
		return modified;
	}

	@Override
	public E remove(int index) {
		if (index <= upperBound && index >= lowerBound) {
			Object old = array[index];
			array[index] = null;
			if (index == upperBound) {
				while (upperBound > 0 && array[upperBound] == null) {
					upperBound--;
				}
			}
			if(index == lowerBound) {
				while (lowerBound < array.length && array[lowerBound] == null) {
					lowerBound++;
				}
			}
			return (E) old;
		}
		return null;
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		if(toIndex-fromIndex<0) {
			throw new ArrayIndexOutOfBoundsException(toIndex-fromIndex);
		}
		if (fromIndex <= upperBound && toIndex >= lowerBound) {
			for (int i = Math.max(fromIndex, lowerBound); i < Math.min(toIndex, upperBound+1); i++) {
				array[i] = null;
			}
			// highestSetObj is always < size()
			if (upperBound < toIndex) {
				while (upperBound > 0 && array[upperBound] == null) {
					upperBound--;
				}
			}
			if(lowerBound > fromIndex) {
				while(lowerBound < array.length && array[lowerBound]==null) {
					lowerBound++;
				}
			}
		}
	}

	private void grow(int i) {
		Object[] newArray = new Object[size() + i];
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}

	@Override
	public int size() {
		return array.length;
	}

	@Override
	public Iterator<E> iterator() {
		return new Itr();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new ListItr(index);
	}

	private class Itr implements Iterator<E> {
		/**
		 * Index of element to be returned by subsequent call to next.
		 */
		int cursor = 0;

		/**
		 * Index of element returned by most recent call to next or previous. Reset to
		 * -1 if this element is deleted by a call to remove.
		 */
		int lastRet = -1;

		/**
		 * The modCount value that the iterator believes that the backing List should
		 * have. If this expectation is violated, the iterator has detected concurrent
		 * modification.
		 */
		int expectedModCount = modCount;

		public boolean hasNext() {
			return cursor <= upperBound;
		}

		public E next() {
			checkForComodification();
			try {
				E next;
				do {
					next = get(cursor);
					cursor++;
				} while (next == null);
				lastRet = cursor - 1;
				return next;
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			if (lastRet < 0) {
				throw new IllegalStateException();
			}
			checkForComodification();

			try {
				DynamicArray.this.remove(lastRet);
				if (lastRet < cursor) {
					cursor--;
				}
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}

		final void checkForComodification() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}

	private class ListItr extends Itr implements ListIterator<E> {
		ListItr(int index) {
			cursor = index;
		}

		public boolean hasPrevious() {
			return cursor >= lowerBound;
		}

		public E previous() {
			checkForComodification();
			try {
				int i;
				E previous;
				do {
					i = cursor - 1;
					previous = get(i);
				} while (previous == null);
				lastRet = (cursor = i);
				return previous;
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		public int nextIndex() {
			return cursor;
		}

		public int previousIndex() {
			return cursor - 1;
		}

		public void set(E e) {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();

			try {
				DynamicArray.this.set(lastRet, e);
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		public void add(E e) {
			checkForComodification();

			try {
				int i = cursor;
				DynamicArray.this.add(i, e);
				lastRet = -1;
				cursor = i + 1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}

}
