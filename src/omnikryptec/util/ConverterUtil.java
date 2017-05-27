package omnikryptec.util;

/**
 *
 * @author Panzer1119
 */
public class ConverterUtil {
        
    public static final com.bulletphysics.util.ObjectArrayList<javax.vecmath.Vector3f> convertToObjectArrayListVector3f(float[] vertices) {
        final com.bulletphysics.util.ObjectArrayList<javax.vecmath.Vector3f> objects = new com.bulletphysics.util.ObjectArrayList<>();
        if(vertices.length % 3 != 0) {
            return objects;
        }
        for(int i = 0; i < (vertices.length - 2); i += 3) {
            objects.add(new javax.vecmath.Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]));
        }
        return objects;
    }
    
    public static final org.lwjgl.util.vector.Vector3f convertVector3fToLWJGL(javax.vecmath.Vector3f vector) {
        return new org.lwjgl.util.vector.Vector3f(vector.x, vector.y, vector.z);
    }
    
    public static final javax.vecmath.Vector3f convertVector3fFromLWJGL(org.lwjgl.util.vector.Vector3f vector) {
        return new javax.vecmath.Vector3f(vector.x, vector.y, vector.z);
    }
    
}
