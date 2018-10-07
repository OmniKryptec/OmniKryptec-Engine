package de.omnikryptec.util.data.pool;

import de.omnikryptec.util.data.FixedStack;

/**
 * A {@link Pool} fixed in size. More instances than the ones that are free can be
 * retrieved. If this Pool is full and an instance is freed, it will not be added to this Pool.
 * 
 * @author pcfreak9000
 *
 * @param <T> the Type of the Object being pooled.
 */
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
	public T retrieve() {
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
