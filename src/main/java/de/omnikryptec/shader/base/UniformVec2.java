package de.omnikryptec.shader.base;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

public class UniformVec2 extends Uniform {

	private float currentX;
	private float currentY;
	private boolean used = false;

	public UniformVec2(String name) {
		super(name);
	}

	public void loadVec2(Vector2f vector) {
		loadVec2(vector.x, vector.y);
	}

	public void loadVec2(float[] array) {
		loadVec2(array[0], array[1]);
	}

	public void loadVec2(float x, float y) {
		if (isFound()&&(!used || x != currentX || y != currentY)) {
			this.currentX = x;
			this.currentY = y;
			used = true;
			GL20.glUniform2f(super.getLocation(), x, y);
		}
	}

}
