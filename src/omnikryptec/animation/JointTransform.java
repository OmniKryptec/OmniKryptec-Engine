package omnikryptec.animation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.util.Quaternion;

/**
 * JointTransform
 * @author Panzer1119
 */
public class JointTransform {
    
    private final Vector3f position;
    private final Quaternion rotation;
    
    /**
     * Creates a JointTransform
     * @param position Vector3f Position
     * @param rotation Quaternion Rotation
     */
    public JointTransform(Vector3f position, Quaternion rotation) {
        this.position = position;
        this.rotation = rotation;
    }
    
    protected final Matrix4f getLocalTransform() {
        final Matrix4f matrix = new Matrix4f();
        matrix.translate(position);
        Matrix4f.mul(matrix, rotation.toRotationMatrix(), matrix);
        return matrix;
    }
    
    protected static final JointTransform interpolate(JointTransform frame_1, JointTransform frame_2, float progression) {
        final Vector3f pos = interpolate(frame_1.position, frame_2.position, progression);
        final Quaternion rot = Quaternion.interpolate(frame_1.rotation, frame_2.rotation, progression);
        return new JointTransform(pos, rot);
    }
    
    private static final Vector3f interpolate(Vector3f start, Vector3f end, float progression) {
        final float x = start.x + (end.x - start.x) * progression;
        final float y = start.y + (end.y - start.y) * progression;
        final float z = start.z + (end.z - start.z) * progression;
        return new Vector3f(x, y, z);
    }
    
}
