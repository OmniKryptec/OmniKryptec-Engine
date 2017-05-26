package omnikryptec.camera;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.storing.Entity;

public class MatrixMath {


	
	public static Matrix4f createTransformationMatrix(Entity e) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(e.getAbsolutePos(), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(e.getAbsoluteRotation().x), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(e.getAbsoluteRotation().y), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(e.getAbsoluteRotation().z), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(e.getScale(), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f setPerspectiveProjection(float fovdeg, float far, float near) {
		return setPerspectiveProjection(fovdeg, far, near, Display.getWidth(), Display.getHeight());
	}

	public static Matrix4f setPerspectiveProjection(float fovdeg, float far, float near, float width, float height) {
		Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = width / height;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(fovdeg / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far - near;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far + near) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near * far) / frustum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
	}

	private float near,far,fov;
	private Vector2f vect = new Vector2f();
	
	public float getFOVDeg(){
		return fov;
	}
	
	public Vector2f getPlanes(){
		vect.x = near;
		vect.y = far;
		return vect;
	}
	
	public static Matrix4f setOrthographicProjection(float left, float right, float bottom, float top, float near,
			float far) {
		Matrix4f m = new Matrix4f();
		float x_orth = 2 / (right - left);
		float y_orth = 2 / (top - bottom);
		float z_orth = -2 / (far - near);

		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = -(far + near) / (far - near);

		m.m00 = x_orth;
		m.m10 = 0;
		m.m20 = 0;
		m.m30 = 0;
		m.m01 = 0;
		m.m11 = y_orth;
		m.m21 = 0;
		m.m31 = 0;
		m.m02 = 0;
		m.m12 = 0;
		m.m22 = z_orth;
		m.m32 = 0;
		m.m03 = tx;
		m.m13 = ty;
		m.m23 = tz;
		m.m33 = 1;
		return m;
	}
	
	public static Matrix4f setOrthographicProjection2D(float x, float y, float width, float height) {
		return setOrthographicProjection( x, x + width, y + height, y, 1, -1);
	}

	public static Matrix4f setOrthographicProjection2D(float x, float y, float width, float height, float near, float far) {
		return setOrthographicProjection(x, x + width, y, y + height, near, far);
	}
}
