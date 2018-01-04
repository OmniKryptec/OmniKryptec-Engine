package omnikryptec.gameobject.component;

import omnikryptec.collision.d2.Rectangle;
import omnikryptec.collision.d2.SimpleCollisionsWorld2D;
import omnikryptec.gameobject.GameObject2D;
import omnikryptec.util.Instance;
import omnikryptec.util.Priority;

@Priority(value = -1)
@ComponentAnnotation(supportedGameObjectClass = GameObject2D.class)
public class CollisionComponent2D extends Component<GameObject2D>{

	private Rectangle bounds;
	
	public CollisionComponent2D(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	@Override
	protected void execute(GameObject2D instance) {
		if(bounds!=null) {
			if(bounds.isColliding()&&bounds.isDynamic()) {
				bounds.restore();
				instance.getTransform().setPosition(bounds.minX, bounds.minY);
			}else {
				bounds.setPosition(instance.getTransform().getPositionSimple().x, instance.getTransform().getPositionSimple().y);
			}
			bounds.saveOld();
			((SimpleCollisionsWorld2D)Instance.getCurrent2DScene().getPhysicsWorld()).addRectangle(bounds);
		}
	}

	@Override
	protected void onDelete(GameObject2D instance) {
		
	}

	public Rectangle getBounds() {
		return bounds;
	}

}
