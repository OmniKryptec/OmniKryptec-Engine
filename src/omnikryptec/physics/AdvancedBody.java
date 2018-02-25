package omnikryptec.physics;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.joml.Vector2f;

import omnikryptec.gameobject.GameObject2D;
import omnikryptec.gameobject.Transform2D;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.ConverterUtil;
import omnikryptec.util.Instance;

public class AdvancedBody extends Body{

	private Vector2f offsetv;
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
			go.getTransform().setPosition(ConverterUtil.convertFromPhysics2D(getTransform().getTranslation(), Instance.getGameSettings().getDouble(GameSettings.PIXELS_PER_METER)).add(offsetv));
//			go.getTransform().setPosition((float) getTransform().getTranslationX()+offsetx,
//					(float) getTransform().getTranslationY()+offsety);
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
		if(offsetv==null) {
			offsetv = new Vector2f();
		}
		offsetv.set(x, y);
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
	
	public AdvancedBody applyVelocityImpulse(Vector2 v) {
		applyImpulse(new Vector2(v).multiply(getMass().getMass()));
		return this;
	}
}
