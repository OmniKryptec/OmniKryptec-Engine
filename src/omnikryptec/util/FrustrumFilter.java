package omnikryptec.util;

import org.joml.FrustumIntersection;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import omnikryptec.gameobject.Entity;

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
	private static Vector4f vec = new Vector4f();
	public static boolean intersects(Entity e){
		tmp = (e.getAdvancedModel().getModel().getModelData().getFurthestPoint()+RADIUS_CORRECTION)*Math.max(e.getTransform().getScale(true).x, Math.max(e.getTransform().getScale(true).y,e.getTransform().getScale(true).z));
		e.getTransformation().transform(vec.set(0,0,0,1));
		return intersects(vec.x, vec.y, vec.z, tmp);
	}
}
