package de.omnikryptec.util.data.pool;

import de.omnikryptec.util.data.FixedStack;

public abstract class FixedPool<T> implements Pool<T> {

	private FixedStack<T> free;
	private boolean checkPoolable = false;

	public FixedPool(Class<T> clazz, int size, boolean prewarm) {
		free = new FixedStack<>(clazz, size);
		if (prewarm) {
			for (int i = 0; i < size; i++) {
				free.push(newObject());
			}
		}
	}

	@Override
	public T obtain() {
		return free.isEmpty() ? newObject() : free.pop();
	}

	@Override
	public void free(T t) {
		if (checkPoolable && t instanceof Poolable) {
			((Poolable) t).reset();
		}
		if (!free.isFull()) {
			free.push(t);
		}
	}

	public void setCheckPoolable(boolean b) {
		this.checkPoolable = b;
	}

	public boolean checkPoolable() {
		return checkPoolable;
	}

	public int getFreeCached() {
		return free.filledSize();
	}
	
	protected abstract T newObject();
}
