package de.omnikryptec.shader.base;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class UniformVec3 extends Uniform {
	private float currentX;
	private float currentY;
	private float currentZ;
	private boolean used = false;

	public UniformVec3(String name) {
		super(name);
	}

	public void loadVec3(Vector3f vector) {
		if (vector != null) {
			loadVec3(vector.x, vector.y, vector.z);
		}
	}

	public void loadVec3(float[] array) {
		loadVec3(array[0], array[1], array[2]);
	}

	public void loadVec3(float x, float y, float z) {
		if (isFound()&&(!used || x != currentX || y != currentY || z != currentZ)) {
			this.currentX = x;
			this.currentY = y;
			this.currentZ = z;
			used = true;
			GL20.glUniform3f(super.getLocation(), x, y, z);
		}
	}

}
