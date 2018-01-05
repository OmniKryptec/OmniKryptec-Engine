package omnikryptec.gameobject.component;

import org.dyn4j.dynamics.Body;

import omnikryptec.gameobject.GameObject2D;
import omnikryptec.gameobject.Transform2D;
import omnikryptec.physics.Dyn4JPhysicsWorld;
import omnikryptec.util.Instance;
import omnikryptec.util.Priority;

@Priority(value = -1)
@ComponentAnnotation(supportedGameObjectClass = GameObject2D.class)
public class PhysicsComponent2D extends Component<GameObject2D>{

	private Body body;
	public boolean enableRotation=true,enablePosition=true;
	private Transform2D offset;
	
	public PhysicsComponent2D(Body b) {
		this.body = b;
	}
	
	@Override
	protected void execute(GameObject2D instance) {
		if(body!=null) {
			if(enablePosition) {
				instance.getTransform().setPosition((float)body.getTransform().getTranslationX(), (float)body.getTransform().getTranslationY());
			}
			if(enableRotation) {
				instance.getTransform().setRotation((float)body.getTransform().getRotation());
			}
			if(offset!=null) {
				instance.getTransform().addTransform(offset, false);
			}
			if(((Dyn4JPhysicsWorld)Instance.getCurrent2DScene().getPhysicsWorld()).raaBody()) {
				((Dyn4JPhysicsWorld)Instance.getCurrent2DScene().getPhysicsWorld()).getWorld().addBody(body);
			}
		}
	}

	public PhysicsComponent2D setOffset(Transform2D t) {
		this.offset = t;
		return this;
	}
	
	@Override
	protected void onDelete(GameObject2D instance) {
		((Dyn4JPhysicsWorld)Instance.getCurrent2DScene().getPhysicsWorld()).getWorld().removeBody(body);
	}
	
	@Override
	protected void added(GameObject2D instance) {
		((Dyn4JPhysicsWorld)Instance.getCurrent2DScene().getPhysicsWorld()).getWorld().addBody(body);
	}

}
