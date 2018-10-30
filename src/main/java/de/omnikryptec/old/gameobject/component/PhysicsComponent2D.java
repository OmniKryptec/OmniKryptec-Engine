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

import de.omnikryptec.old.gameobject.GameObject2D;
import de.omnikryptec.old.physics.AdvancedBody;
import de.omnikryptec.old.physics.Dyn4JPhysicsWorld;
import de.omnikryptec.old.util.Instance;
import de.omnikryptec.old.util.Priority;

@Priority(value = -1)
@ComponentAnnotation(supportedGameObjectClass = GameObject2D.class)
public class PhysicsComponent2D extends Component<GameObject2D> {

    private AdvancedBody body;

    public PhysicsComponent2D(AdvancedBody b) {
	this.body = b;
    }

    @Override
    protected void execute(GameObject2D instance) {
	if (body != null) {
	    body.setPositionOf(instance);
	    Dyn4JPhysicsWorld world = (Dyn4JPhysicsWorld) Instance.getCurrent2DScene().getPhysicsWorld();
	    if (world.raaBody() && !world.getWorld().containsBody(body)) {
		world.getWorld().addBody(body);
	    }
	}
    }

    @Override
    protected void onDelete(GameObject2D instance) {
	((Dyn4JPhysicsWorld) Instance.getCurrent2DScene().getPhysicsWorld()).getWorld().removeBody(body);
    }

    @Override
    protected void added(GameObject2D instance) {
	((Dyn4JPhysicsWorld) Instance.getCurrent2DScene().getPhysicsWorld()).getWorld().addBody(body);
    }

    public AdvancedBody getBody() {
	return body;
    }

}
