package omnikryptec.util;

import org.joml.FrustumIntersection;
import org.joml.Matrix4fc;

import omnikryptec.gameobject.gameobject.Entity;

public class FrustrumFilter {
	
	private static final float RADIUS_CORRECTION=1;
	
	private static FrustumIntersection intersection = new FrustumIntersection();
	private static boolean isenabled=true;
	
	public static void setProjViewMatrices(Matrix4fc m){
		isenabled = Instance.getGameSettings().useFrustrumCulling();
		intersection.set(m);
	}
	
	public static boolean intersects(float x, float y, float z, float boundingsphererad){
		if(!isenabled){
			return true;
		}
		return intersection.testSphere(x, y, z, boundingsphererad);
	}
	
	private static float tmp;
	public static boolean intersects(Entity e){
		tmp = (e.getAdvancedModel().getModel().getModelData().getFurthestPoint()+RADIUS_CORRECTION)*Math.max(e.getTransform().getScale(true).x, Math.max(e.getTransform().getScale(true).y,e.getTransform().getScale(true).z));
		return intersects(e.getTransform().getPosition(true).x, e.getTransform().getPosition(true).y, e.getTransform().getPosition(true).z, tmp);
	}
}
