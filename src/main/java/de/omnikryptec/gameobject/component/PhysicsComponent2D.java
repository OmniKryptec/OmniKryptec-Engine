package de.omnikryptec.gameobject.component;

import de.omnikryptec.gameobject.GameObject2D;
import de.omnikryptec.physics.AdvancedBody;
import de.omnikryptec.physics.Dyn4JPhysicsWorld;
import de.omnikryptec.util.Instance;
import de.omnikryptec.util.Priority;

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
