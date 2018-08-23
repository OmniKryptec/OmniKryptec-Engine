package de.omnikryptec.shader.base;

import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;
import org.lwjgl.opengl.GL20;

public abstract class Uniform {

	private static final int NOT_FOUND = -1;

	private String name;
	private int location;
	private boolean muted=false;
	private boolean isfound=false;
	
	protected Uniform(String name) {
		this(name, false);
	}
	
	protected Uniform(String name, boolean mute) {
		this.name = name;
	}

	protected void storeUniformLocation(Shader shader) {
		location = GL20.glGetUniformLocation(shader.getId(), name);
		if(location == NOT_FOUND) {
			isfound = false;
			if (!muted && Logger.isDebugMode()) {
				Logger.log(shader.getName() + ": No uniform variable called " + name + " found!", LogLevel.WARNING);
			}
		}else {
			isfound = true;
		}
	}

	protected int getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "Name: " + name + " Location: " + location;
	}

	public Uniform mute() {
		this.muted = true;
		return this;
	}
	
	public boolean isFound() {
		return isfound;
	}
	
}
