package omnikryptec.util;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import omnikryptec.display.Display;
import omnikryptec.gameobject.gameobject.Entity;

import java.util.Random;

import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Matrix3f;

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

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
    
    public static Vector2f rotateAround(float x, float y, float radius, double radians){
    	Vector2f vec = new Vector2f((float) (radius*Math.sin(radians)), (float)(radius*Math.cos(radians)));
    	vec.add(x, y);
    	return vec;
    }
    
    
	private static Vector4f tmp4f = new Vector4f();
	private static Vector3f rotateAxis = new Vector3f();
	private static Matrix4f rotationMatrix = new Matrix4f();
	private static Matrix3f helpmatrix = new Matrix3f();
	
	public static Vector3f generateRandomUnitVectorWithinCone(Random random, Vector3f coneDirection, double coneangle) {
		float cosAngle = (float) Math.cos(coneangle);
		double theta = getRandomRotationf(random);
		float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));

		tmp4f.set(x, y, z, 1);
		if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
			coneDirection.cross(Maths.Z, rotateAxis);
			rotateAxis.normalize();
			float rotateAngle = (float) Math.acos(coneDirection.dot(Maths.Z));
			rotationMatrix.identity();
			rotationMatrix.rotate(-rotateAngle, rotateAxis);
			rotationMatrix.transform(tmp4f);
		} else if (coneDirection.z == -1) {
			tmp4f.z *= -1;
		}
		return new Vector3f(tmp4f.x, tmp4f.y, tmp4f.z).normalize();
	}

	public static Vector3f generateRandomUnitVector(Random random) {
		double theta = getRandomRotationf(random);
		float z = (random.nextFloat() * 2) - 1;
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		return new Vector3f((float) (rootOneMinusZSquared * Math.cos(theta)), (float) (rootOneMinusZSquared * Math.sin(theta)), z).normalize();
	}
	
	public static float getErroredValue(Random random, float average, float errorMargin) {
		float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
		return average + offset;
	}
	
	public static Vector3f getRandomPointOnLine(Random random, Vector3f direction, Vector3f center, float linelength){
		Vector3f vec = direction.normalize(new Vector3f()).mul(linelength/2);
		vec.mul(random.nextFloat()*2-1);
		return vec.add(center);
	}
	
	public static Vector3f getRandomPointOnLine(Random random, Vector3f p1, Vector3f p2){
		Vector3f vec = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
		vec.mul(random.nextFloat());
		return vec.add(p1);
	}
	
	public static Vector3f getRandomPointInCircle(Random random, Vector3f middle, float radius, Vector3f direction){
		Vector3f directionn = direction.normalize(new Vector3f());
		double rotation = getRandomRotation(random);
		double calculatedradius = (radius * Math.sqrt(random.nextDouble()));
		double acosdot = Math.acos(directionn.dot(Y));
		Vector3f cross = Maths.Y.cross(directionn, new Vector3f()).normalize();
		if(acosdot==0||(Float.isNaN(cross.x)&&Float.isNaN(cross.y)&&Float.isNaN(cross.z))){
			return new Vector3f((float) ((Math.cos(rotation)*calculatedradius)), 0, (float) ((Math.sin(rotation)*calculatedradius))).add(middle);
		}else{
			return helpmatrix.identity().rotate(new AxisAngle4f((float) acosdot, cross.x, cross.y, cross.z)).transform(new Vector3f((float) ((Math.cos(rotation)*calculatedradius)), 0, (float) ((Math.sin(rotation)*calculatedradius)))).add(middle);		
		}	
	}
	
	public static Vector3f getRandomPointInCircle(Random random, Vector3f middle, float radius){
		double rotation = getRandomRotation(random);
		double calculatedradius = (radius * Math.sqrt(random.nextDouble()));
		return new Vector3f((float) (middle.x+(Math.cos(rotation)*calculatedradius)), middle.y, (float) (middle.z+(Math.sin(rotation)*calculatedradius)));
	}
	
	public static Vector3f getRandomPointInSphere(Random random, Vector3f middle, float radius){
		float calculatedradius = (float)(radius * Math.sqrt(random.nextDouble()));
		return generateRandomUnitVector(random).mul(calculatedradius);
	}
	
	public static double getRandomRotation(Random r){
		return (r.nextDouble() * 2 * Math.PI);
	}
	
	public static float getRandomRotationf(Random r){
		return (float)getRandomRotation(r);
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
