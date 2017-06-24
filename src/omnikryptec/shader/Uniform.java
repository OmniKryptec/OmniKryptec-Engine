package omnikryptec.shader;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {

	private static final int NOT_FOUND = -1;

	private String name;
	private int location;

	protected Uniform(String name) {
		this.name = name;
	}

	protected void storeUniformLocation(Shader shader) {
		location = GL20.glGetUniformLocation(shader.getId(), name);
		if (location == NOT_FOUND) {
			System.err.println(shader.getName() + ": No uniform variable called " + name + " found!");
		}
	}

	protected int getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "Name: " + name + " Location: " + location;
	}

}
