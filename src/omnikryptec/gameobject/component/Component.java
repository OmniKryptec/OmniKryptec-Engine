package omnikryptec.gameobject.component;

import omnikryptec.gameobject.GameObject;

/**
 * Component interface
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
public abstract class Component<T extends GameObject> {
	
	private final Class<? extends GameObject> supported;
	
	protected Component() {
    	if(getClass().isAnnotationPresent(ComponentAnnotation.class)) {
    		supported = getClass().getAnnotation(ComponentAnnotation.class).supportedGameObjectClass();
    	}else {
    		supported = GameObject.class;
    	}
	}
	
	
	@SuppressWarnings("unchecked")
	public final void runOn(GameObject g) {
		execute((T)g);
	}
	
	@SuppressWarnings("unchecked")
	public final void deleteOp(GameObject g) {
		onDelete((T)g);
	}
	
	public final boolean supportsGameObject(GameObject g) {
		return supported.isAssignableFrom(g.getClass());
	}
	
	/**
	 * Called on frame update of the parent GameObject
	 * 
	 * @param instance
	 *            GameObject Parent GameObject
	 */
	protected abstract void execute(T instance);

	/**
	 * Called on deletion of the parent GameObject
	 * 
	 * @param instance
	 *            GameObject Parent GameObject
	 */
	protected abstract void onDelete(T instance);

	/**
	 * Returns the level of this component
	 * 
	 * @return Float Level of the execution (negative = before logic execution,
	 *         positive = after logic execution)
	 */
	public float getLevel() {
		return 0;
	}
}
