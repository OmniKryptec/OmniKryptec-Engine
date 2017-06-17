package omnikryptec.component;

import omnikryptec.entity.GameObject;

/**
 * Component interface
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
public interface Component {

	/**
	 * Called on frame update of the parent GameObject
	 * 
	 * @param instance
	 *            GameObject Parent GameObject
	 */
	void execute(GameObject instance);

	/**
	 * Called on deletion of the parent GameObject
	 * 
	 * @param instance
	 *            GameObject Parent GameObject
	 */
	void onDelete(GameObject instance);

	/**
	 * Returns the level of this component
	 * 
	 * @return Float Level of the execution (negative = before logic execution,
	 *         positive = after logic execution)
	 */
	float getLevel();
}
