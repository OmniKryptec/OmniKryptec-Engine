package de.omnikryptec.gameobject.component;

import de.omnikryptec.gameobject.GameObject;
import de.omnikryptec.util.Util;

/**
 * Component interface
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
public abstract class Component<T extends GameObject> {
	
	private final Class<? extends GameObject> supported;
	private final float prio;
	
	protected Component() {
    	if(getClass().isAnnotationPresent(ComponentAnnotation.class)) {
    		supported = getClass().getAnnotation(ComponentAnnotation.class).supportedGameObjectClass();
    	}else {
    		supported = GameObject.class;
    	}
    	prio = Util.extractPrio(getClass(), 0);
	}
	
	
	
	public final void runOn(GameObject g) {
		execute((T)g);
	}
	
	
	public final void deleteOp(GameObject g) {
		onDelete((T)g);
	}
	
	
	public final void addedOp(GameObject g) {
		added((T)g);
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

	protected void added(T instance) {
		
	}
	
	/**
	 * Returns the level of this component
	 * 
	 * @return Float Level of the execution (negative = before logic execution,
	 *         positive = after logic execution)
	 */
	public final float getPrio() {
		return prio;
	}
}
