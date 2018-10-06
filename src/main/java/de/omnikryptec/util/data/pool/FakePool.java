package de.omnikryptec.util.data.pool;

public abstract class FakePool<T> extends Pool<T>{

	@Override
	public T next() {
		return newObject();
	}

	@Override
	public void free(T t) {
	}

}
