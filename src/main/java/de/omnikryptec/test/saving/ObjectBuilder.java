package de.omnikryptec.test.saving;

/**
 *
 * @author Panzer1119
 */
public interface ObjectBuilder<T> {

	public ObjectBuilder loadDataMap(DataMap data);

	public T build();

}
