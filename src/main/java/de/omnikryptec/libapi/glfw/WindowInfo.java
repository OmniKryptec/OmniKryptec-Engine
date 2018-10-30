package de.omnikryptec.libapi.glfw;

public abstract class WindowInfo<T extends WindowInfo<?>> {

    private int width = 800;
    private int height = 600;
    private boolean fullscreen = false;
    private boolean resizeable = true;
    private boolean lockAspectRatio = false;
    private String name = "Display";

    protected WindowInfo() {
    }

    public abstract Window<T> createWindow();

    public T setWidth(int width) {
	this.width = width;
	return (T) this;
    }

    public T setHeight(int height) {
	this.height = height;
	return (T) this;
    }

    public T setFullscreen(boolean fullscreen) {
	this.fullscreen = fullscreen;
	return (T) this;
    }

    public T setResizeable(boolean resizeable) {
	this.resizeable = resizeable;
	return (T) this;
    }

    public T setLockAspectRatio(boolean lockAspectRatio) {
	this.lockAspectRatio = lockAspectRatio;
	return (T) this;
    }

    public T setName(String name) {
	this.name = name;
	return (T) this;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public boolean isFullscreen() {
	return fullscreen;
    }

    public boolean isResizeable() {
	return resizeable;
    }

    public boolean isLockAspectRatio() {
	return lockAspectRatio;
    }

    public String getName() {
	return name;
    }

}
