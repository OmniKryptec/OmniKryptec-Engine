package de.omnikryptec.libapi.glfw;

public class OpenGLWindowInfo extends WindowInfo<OpenGLWindowInfo> {

	private int majVersion = 1, minVersion = 0;

	public OpenGLWindowInfo() {
	}

	public int getMajVersion() {
		return majVersion;
	}

	public OpenGLWindowInfo setMajVersion(int majVersion) {
		this.majVersion = majVersion;
		return this;
	}

	public int getMinVersion() {
		return minVersion;
	}

	public OpenGLWindowInfo setMinVersion(int minVersion) {
		this.minVersion = minVersion;
		return this;
	}

}