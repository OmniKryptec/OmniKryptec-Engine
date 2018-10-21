package de.omnikryptec.graphics.display;

public class WindowInfo {

	private int width = 800;
	private int height = 600;
	private boolean fullscreen = false;
	private boolean resizeable = true;
	private boolean lockAspectRatio = false;
	private String name = "Display";

	public WindowInfo setWidth(int width) {
		this.width = width;
		return this;
	}

	public WindowInfo setHeight(int height) {
		this.height = height;
		return this;
	}

	public WindowInfo setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
		return this;
	}

	public WindowInfo setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
		return this;
	}

	public WindowInfo setLockAspectRatio(boolean lockAspectRatio) {
		this.lockAspectRatio = lockAspectRatio;
		return this;
	}

	public WindowInfo setName(String name) {
		this.name = name;
		return this;
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
