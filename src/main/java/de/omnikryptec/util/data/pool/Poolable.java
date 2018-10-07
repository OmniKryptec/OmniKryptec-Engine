package de.omnikryptec.util.data.pool;

/**
 * See {@link Pool}.
 * @author pcfreak9000
 *
 */
public interface Poolable {
	
	/**
	 * Resets this {@link Poolable} to be ready to be reused.
	 */
	void reset();
}
