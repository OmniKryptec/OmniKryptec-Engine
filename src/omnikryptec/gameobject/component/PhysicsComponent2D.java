package omnikryptec.gameobject.component;

import org.dyn4j.dynamics.Body;

import omnikryptec.gameobject.GameObject2D;
import omnikryptec.gameobject.Transform2D;
import omnikryptec.physics.Dyn4JPhysicsWorld;
import omnikryptec.util.Instance;
import omnikryptec.util.Priority;

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

}
