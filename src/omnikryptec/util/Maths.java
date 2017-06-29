package omnikryptec.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import omnikryptec.display.Display;
import omnikryptec.entity.Entity;

public class Maths {

    public static final Vector3f X = new Vector3f(1, 0, 0);
    public static final Vector3f Y = new Vector3f(0, 1, 0);
    public static final Vector3f Z = new Vector3f(0, 0, 1);
    public static final Vector3f ZERO = new Vector3f(0, 0, 0);
    public static final Vector3f ONE = new Vector3f(1, 1, 1);

    public static Matrix4f createTransformationMatrix(Entity entity, Matrix4f old) {
        return createTransformationMatrix(entity.getAbsolutePos(), entity.getAbsoluteRotation(), entity.getScale(), old);
    }
    public static Matrix4f createTransformationMatrix(Vector3f position, Vector3f rotation, Vector3f scale) {
    	return createTransformationMatrix(position, rotation, scale, null);
    }

    
    public static Matrix4f createTransformationMatrix(Vector3f position, Vector3f rotation, Vector3f scale, Matrix4f matrix) {
        if(matrix==null){
        	matrix = new Matrix4f();
        }
        matrix.identity();
        matrix.translate(position);
        matrix.rotate((float) Math.toRadians(rotation.x), X);
        matrix.rotate((float) Math.toRadians(rotation.y), Y);
        matrix.rotate((float) Math.toRadians(rotation.z), Z);
        matrix.scale(scale);
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(rotation.x), X);
        matrix.rotate((float) Math.toRadians(rotation.y), Y);
        matrix.rotate((float) Math.toRadians(rotation.z), Z);
        matrix.scale(scale);
        return matrix;
    }

    public static Matrix4f createEmptyTransformationMatrix() {
        return createEmptyTransformationMatrix(ZERO);
    }

    public static Matrix4f createEmptyTransformationMatrix(Vector3f pos) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(pos);
        return matrix;
    }

    public static Matrix4f setPerspectiveProjection(float fovdeg, float far, float near) {
        return setPerspectiveProjection(fovdeg, far, near, Display.getWidth(), Display.getHeight());
    }

    public static Matrix4f setPerspectiveProjection(float fovdeg, float far, float near, float width, float height) {
        float aspectRatio = width / height;
        return new Matrix4f().setPerspective((float) Math.toRadians(fovdeg), aspectRatio, near, far);
//    	Matrix4f projectionMatrix = new Matrix4f();
//    	 
//        float aspectRatio = width / height;
// 
//        float y_scale = (float) ((1f / Math.tan(Math.toRadians(fovdeg / 2f))));
// 
//        float x_scale = y_scale / aspectRatio;
// 
//        float frustum_length = far - near;
// 
//
// 
//        projectionMatrix.m00( x_scale);
// 
//        projectionMatrix.m11 ( y_scale);
// 
//        projectionMatrix.m22 ( -((far + near) / frustum_length));
// 
//        projectionMatrix.m23 ( -1);
// 
//        projectionMatrix.m32 ( -((2 * near * far) / frustum_length));
// 
//        projectionMatrix.m33 (0);
// 
//        return projectionMatrix;
 
    }

    public static Matrix4f setOrthographicProjection(float left, float right, float bottom, float top, float near,
            float far) {
        return new Matrix4f().setOrtho(left, right, bottom, top, near, far);
    }

    public static Matrix4f setOrthographicProjection2D(float x, float y, float width, float height) {
        return setOrthographicProjection(x, x + width, y + height, y, 1, -1);
    }

    public static Matrix4f setOrthographicProjection2D(float x, float y, float width, float height, float near,
            float far) {
        return setOrthographicProjection(x, x + width, y, y + height, near, far);
    }

    public static boolean fastEquals3f(Vector3f one, Vector3f sec) {
        return one != null && sec != null && one.x == sec.x && one.y == sec.y && one.z == sec.z;
    }

    public static final float[] intArrayToFloatArray(int[] input) {
        final float[] output = new float[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    public static final int[] floatArrayToIntArray(float[] input) {
        final int[] output = new int[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (int) (input[i] + 0.5);
        }
        return output;
    }

    public static final float clamp(float input, float min, float max) {
        return Math.min(max, Math.max(min, input));
    }

    public static final int clamp(int input, int min, int max) {
        return Math.min(max, Math.max(min, input));
    }

    // public static double getRelativizer(double velocity, double maxVelocity){
    // if(velocity>maxVelocity){
    // velocity = maxVelocity;
    // }
    // if(velocity<0){
    // velocity = 0;
    // }
    // return Math.sqrt(1- (velocity*velocity)/(maxVelocity*maxVelocity));
    // }
}
