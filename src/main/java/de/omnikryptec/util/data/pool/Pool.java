package de.omnikryptec.util.data.pool;

public interface Pool<T> {

	T obtain();
	void free(T t);
	
}
