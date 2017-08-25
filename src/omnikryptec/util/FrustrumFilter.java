package omnikryptec.util;

import org.joml.FrustumIntersection;
import org.joml.Vector4f;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.Entity;

public class FrustrumFilter {
	
	private static final float RADIUS_CORRECTION=3f;
	
	private FrustumIntersection intersection = new FrustumIntersection();
	private boolean isenabled=true;
	private Camera cam;
	
	public void setCamera(Camera c){
		isenabled = Instance.getGameSettings().useFrustrumCulling();
		cam = c;
		intersection.set(cam.getProjectionViewMatrix());
	}
	
	public boolean intersects(float x, float y, float z, float boundingsphererad){
		if(!isenabled){
			return true;
		}
		return intersection.testSphere(x, y, z, boundingsphererad);
	}
	
	private float tmp;
	private Vector4f vec = new Vector4f();
	public boolean intersects(Entity e, boolean checkRenderRange){
		if(!isenabled){
			return true;
		}
		tmp = (e.getAdvancedModel().getModel().getModelData().getFurthestPoint()*RADIUS_CORRECTION)*Math.max(e.getTransform().getScale(true).x, Math.max(e.getTransform().getScale(true).y,e.getTransform().getScale(true).z));
		e.getTransformation().transform(vec.set(0,0,0,1));
		return intersects(vec.x, vec.y, vec.z, tmp)&&(checkRenderRange?RenderUtil.inRenderRange(e, cam):true);
	}
}
