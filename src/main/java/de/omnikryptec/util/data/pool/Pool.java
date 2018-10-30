package de.omnikryptec.util.data.pool;

/**
 * Collection of Objects to reuse multiple times. The objects can, but do not
 * neccasserily have to, implement {@link Poolable}.
 * 
 * @author pcfreak9000
 *
 * @param <T> the Type of the Object being pooled.
 */
public abstract class Pool<T> {

    /**
     * Get a free object. If none is available, a new one might be created via
     * {@link #newObject()} or an exception might be thrown.
     * 
     * @return a fresh-to-use instance
     * @throws NoFreeInstanceException if no free instance is available and this
     *                                 {@link Pool} does not permit new instances
     *                                 on-the-fly.
     */
    public abstract T retrieve() throws NoFreeInstanceException;

    /**
     * Create a new object for this {@link Pool}. Does not neccassarily add it to
     * this {@link Pool}.
     * 
     * @return a new instance
     */
    protected abstract T newObject();

    /**
     * Return a used instance to this {@link Pool}. If {@code T} implements
     * {@link Poolable}, it must be {@link Poolable#reset()} here. The freed
     * instance might be reused by {@link #retrieve()}.
     * 
     * @param the instance to free
     */
    public abstract void free(T t);

    /**
     * Returns the amount of free objects in this {@link Pool}. If there are
     * infinite free objects, a number equal to or greater than 1 must be returned.
     * 
     * @return amount of free objects
     */
    public abstract int available();

}
