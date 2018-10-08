package de.omnikryptec.util.data.pool;

/**
 * A fake Pool. {@link #retrieve()} will always return a newly created Object.
 * {@link #free(Object)} is unused. This might be useful for testing unpooled vs pooled
 * systems.
 * 
 * @author pcfreak9000
 *
 * @param <T> the Type of the Object being pooled.
 */
public abstract class FakePool<T> extends Pool<T> {

	@Override
	public T retrieve() {
		return newObject();
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void free(T t) {
	}

	/**
	 * Always returns 1
	 */
	@Override
	public int available() {
		return 1;
	}
}
