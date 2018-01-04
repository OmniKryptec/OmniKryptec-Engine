package omnikryptec.util;

import org.dyn4j.geometry.Vector2;
import org.joml.Vector2f;

/**
 *
 * @author Panzer1119
 */
public class ConverterUtil {

	public static final com.bulletphysics.util.ObjectArrayList<javax.vecmath.Vector3f> convertToObjectArrayListVector3f(
			float[] vertices) {
		final com.bulletphysics.util.ObjectArrayList<javax.vecmath.Vector3f> objects = new com.bulletphysics.util.ObjectArrayList<>();
		if (vertices.length % 3 != 0) {
			return objects;
		}
		for (int i = 0; i < (vertices.length - 2); i += 3) {
			objects.add(new javax.vecmath.Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]));
		}
		return objects;
	}

	public static final org.joml.Vector3f convertVector3fToLWJGL(javax.vecmath.Vector3f vector) {
		return new org.joml.Vector3f(vector.x, vector.y, vector.z);
	}

	public static final javax.vecmath.Vector3f convertVector3fFromLWJGL(org.joml.Vector3f vector) {
		return new javax.vecmath.Vector3f(vector.x, vector.y, vector.z);
	}
	
	public static final javax.vecmath.Quat4f convertQuat4fFromLWJGL(org.joml.Quaternionf q){
		return new javax.vecmath.Quat4f(q.x, q.y, q.z, q.w);
	}

	public static final Vector2f convertVector3fToLWJGL(Vector2 v) {
		return new Vector2f((float)v.x, (float)v.y);
	}
	
	public static final Vector2 convertVector2fFromLWJGL(Vector2f v) {
		return new Vector2(v.x, v.y);
	}
	
}
