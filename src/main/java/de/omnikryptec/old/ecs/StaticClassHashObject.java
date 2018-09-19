package de.omnikryptec.old.ecs;

/**
 * {@link #hashCode()} is now an ID for this class's child.
 * @author pcfreak9000
 *
 */
public abstract class StaticClassHashObject {

	private final int HASHCODE = this.getClass().hashCode();
	
	@Override
	public final int hashCode() {
		return HASHCODE;
	}
}
