package omnikryptec.shader;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

public class UniformVec4 extends Uniform {

	private float[] old = new float[4];
	private boolean used = false;

	public UniformVec4(String name) {
		super(name);
	}

	public void loadVec4(Vector4f vector) {
		loadVec4(vector.x, vector.y, vector.z, vector.w);
	}

	public void loadVec4(float[] array) {
		loadVec4(array[0], array[1], array[2], array[3]);
	}

	public void loadVec4(float x, float y, float z, float w) {
		if (!used || x != old[0] || y != old[1] || z != old[2] || w != old[3]) {
			GL20.glUniform4f(super.getLocation(), x, y, z, w);
			old[0] = x;
			old[1] = y;
			old[2] = z;
			old[3] = w;
			used = true;
		}
	}

}
