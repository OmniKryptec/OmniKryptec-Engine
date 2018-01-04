package omnikryptec.gameobject.component;

import org.dyn4j.UnitConversion;
import org.dyn4j.dynamics.Body;

import omnikryptec.gameobject.GameObject2D;

@ComponentAnnotation(supportedGameObjectClass = GameObject2D.class)
public class PhysicsComponent2D extends Component<GameObject2D>{

	private Body body;
	
	@Override
	protected void execute(GameObject2D instance) {
		//instance.getTransform().setPosition(body., y)
	}

	@Override
	protected void onDelete(GameObject2D instance) {
		
	}

}
