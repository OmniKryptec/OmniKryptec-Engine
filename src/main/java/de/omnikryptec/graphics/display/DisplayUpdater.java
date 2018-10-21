package de.omnikryptec.graphics.display;

import de.omnikryptec.libapi.glfw.StateManager;

public class DisplayUpdater {

	private double updateTime = 0;
	private double fpstime = 0;
	private double deltatime = 0;
	private double lasttime = 0;
	private double frontruntime = 0;

	private long framecount = 0;

	private long fps1 = 0, fps2 = 0;
	private boolean fps = true;

	public void update(Display display) {
		if (framecount == 0) {
			lasttime = StateManager.active().getTime();
			fpstime = StateManager.active().getTime() / 1000.0;
		}
		double currentFrameTime = StateManager.active().getTime();
		deltatime = (currentFrameTime - lasttime) / 1000.0;
		frontruntime += deltatime;
		lasttime = currentFrameTime;
		double tmptime = StateManager.active().getTime();
		display.update();
		updateTime = StateManager.active().getTime() - tmptime;
		framecount++;
		if (fps) {
			fps1++;
		} else {
			fps2++;
		}
		if (frontruntime - fpstime >= 1.0) {
			fpstime = frontruntime;
			if (fps) {
				fps = false;
				fps2 = 0;
			} else {
				fps = true;
				fps1 = 0;
			}
		}
	}

	public long getFrameCount() {
		return framecount;
	}

	public double getFrontRunningTime() {
		return frontruntime;
	}

	public long getFPS() {
		return fps ? fps2 : fps1;
	}

	public double getDeltaTime() {
		return deltatime;
	}

	public double getUpdateTime() {
		return updateTime;
	}

}
