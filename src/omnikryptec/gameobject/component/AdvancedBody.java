package omnikryptec.gameobject.component;

import org.dyn4j.dynamics.Body;

import omnikryptec.gameobject.GameObject2D;
import omnikryptec.gameobject.Transform2D;

public class AdvancedBody extends Body{

	private float offsetx,offsety;
	private boolean enableRotation = true, enablePosition = true;
	private Transform2D offset;
	
	public AdvancedBody() {
		super();
	}
	
	public AdvancedBody(int fixcount) {
		super(fixcount);
	}
	
	public AdvancedBody setPositionOf(GameObject2D go) {
		if (enablePosition) {
			go.getTransform().setPosition((float) getTransform().getTranslationX()+offsetx,
					(float) getTransform().getTranslationY()+offsety);
		}
		if (enableRotation) {
			go.getTransform().setRotation((float) getTransform().getRotation());
		}
		if (offset != null) {
			go.getTransform().addTransform(offset, false);
		}
		return this;
	}
	
	public AdvancedBody setOffsetTransform(Transform2D t) {
		this.offset = t;
		return this;
	}
	
	public AdvancedBody setOffsetXY(float x, float y) {
		offsetx = x;
		offsety = y;
		return this;
	}
	
	public AdvancedBody enableRotationSet(boolean b) {
		this.enableRotation = b;
		return this;
	}
	
	public AdvancedBody enablePositionSet(boolean b) {
		this.enablePosition = b;
		return this;
	}
}
