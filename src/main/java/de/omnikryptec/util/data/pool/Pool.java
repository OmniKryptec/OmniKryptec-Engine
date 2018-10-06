package de.omnikryptec.util.data.pool;

public abstract class Pool<T> {

	public abstract T next();
	protected abstract T newObject();
	public abstract void free(T t);
	
}
