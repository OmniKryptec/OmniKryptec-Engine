package de.omnikryptec.util.data.pool;

import de.omnikryptec.util.data.FixedStack;

public abstract class FixedPool<T> extends Pool<T> {

	private FixedStack<T> free;
	private boolean poolable = false;

	public FixedPool(Class<T> clazz, int size, boolean prewarm) {
		poolable = Poolable.class.isAssignableFrom(clazz);
		free = new FixedStack<>(clazz, size);
		if (prewarm) {
			for (int i = 0; i < size; i++) {
				free.push(newObject());
			}
		}
	}

	@Override
	public T next() {
		return free.isEmpty() ? newObject() : free.pop();
	}

	@Override
	public void free(T t) {
		if (poolable) {
			((Poolable) t).reset();
		}
		if (!free.isFull()) {
			free.push(t);
		}
	}

	public int getFreeCached() {
		return free.filledSize();
	}
}
