/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.gameobject.component;

import de.omnikryptec.old.gameobject.GameObject;
import de.omnikryptec.old.util.Util;

/**
 * Component interface
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
public abstract class Component<T extends GameObject> {

    private final Class<? extends GameObject> supported;
    private final float prio;

    protected Component() {
	if (getClass().isAnnotationPresent(ComponentAnnotation.class)) {
	    supported = getClass().getAnnotation(ComponentAnnotation.class).supportedGameObjectClass();
	} else {
	    supported = GameObject.class;
	}
	prio = Util.extractPrio(getClass(), 0);
    }

    public final void runOn(GameObject g) {
	execute((T) g);
    }

    public final void deleteOp(GameObject g) {
	onDelete((T) g);
    }

    public final void addedOp(GameObject g) {
	added((T) g);
    }

    public final boolean supportsGameObject(GameObject g) {
	return supported.isAssignableFrom(g.getClass());
    }

    /**
     * Called on frame update of the parent GameObject
     * 
     * @param instance GameObject Parent GameObject
     */
    protected abstract void execute(T instance);

    /**
     * Called on deletion of the parent GameObject
     * 
     * @param instance GameObject Parent GameObject
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
