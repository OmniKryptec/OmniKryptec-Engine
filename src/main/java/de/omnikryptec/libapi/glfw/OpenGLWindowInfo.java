package de.omnikryptec.libapi.glfw;

public class OpenGLWindowInfo extends WindowInfo<OpenGLWindowInfo> {

    private int majVersion = 1, minVersion = 0;
    private boolean vsync = true;

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

    public boolean isVsync() {
	return vsync;
    }

    public OpenGLWindowInfo setVSync(boolean enabled) {
	this.vsync = enabled;
	return this;
    }

    @Override
    public OpenGLWindow createWindow() {
	return new OpenGLWindow(this);
    }

}
