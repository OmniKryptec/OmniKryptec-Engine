package omnikryptec.gameobject.component;

import omnikryptec.gameobject.GameObject;

/**
 * Component interface
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
public interface Component<T extends GameObject> {

	/**
	 * Called on frame update of the parent GameObject
	 * 
	 * @param instance
	 *            GameObject Parent GameObject
	 */
	void execute(T instance);

	/**
	 * Called on deletion of the parent GameObject
	 * 
	 * @param instance
	 *            GameObject Parent GameObject
	 */
	void onDelete(T instance);

	/**
	 * Returns the level of this component
	 * 
	 * @return Float Level of the execution (negative = before logic execution,
	 *         positive = after logic execution)
	 */
	float getLevel();
}
