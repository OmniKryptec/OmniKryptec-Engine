package de.omnikryptec.util;

import de.omnikryptec.settings.GameSettings;
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

	public static final Vector2f convertVector2fToLWJGL(Vector2 v) {
		return new Vector2f((float)v.x, (float)v.y);
	}
	
	public static final Vector2 convertVector2fFromLWJGL(Vector2f v) {
		return new Vector2(v.x, v.y);
	}
	
    public static final Vector2f convertFromPhysics2D(Vector2 vec, double pixelsPerMeter) {
    	return new Vector2f((float)(vec.x*pixelsPerMeter), (float)(vec.y*pixelsPerMeter));
    }
    
    public static final Vector2 convertToPhysics2D(Vector2f vec, double pixelsPerMeter) {
    	pixelsPerMeter = 1.0/pixelsPerMeter;
    	return new Vector2(vec.x*pixelsPerMeter, vec.y*pixelsPerMeter);
    }
    public static final Vector2f convertFromPhysics2D(Vector2 vec) {
    	return convertFromPhysics2D(vec, Instance.getGameSettings().getDouble(GameSettings.PIXELS_PER_METER));
    }
    
    public static final Vector2 convertToPhysics2D(Vector2f vec) {
    	return convertToPhysics2D(vec, Instance.getGameSettings().getDouble(GameSettings.PIXELS_PER_METER));
    }

	public static final double convertToPhysics2D(float f, double pixelsPerMeter) {
		return f/pixelsPerMeter;
	}
	
	public static final double convertToPhysics2D(float f) {
		return convertToPhysics2D(f, Instance.getGameSettings().getDouble(GameSettings.PIXELS_PER_METER));
	}
	
	public static final float convertFromPhysics2D(double d) {
		return convertFromPhysics2D(d, Instance.getGameSettings().getDouble(GameSettings.PIXELS_PER_METER));
	}

	public static float convertFromPhysics2D(double d, double pixelsPerMeter) {
		return (float) (d*pixelsPerMeter);
	}
}
