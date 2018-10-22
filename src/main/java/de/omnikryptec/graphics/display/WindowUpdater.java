package de.omnikryptec.graphics.display;

import de.omnikryptec.libapi.glfw.GLFWManager;
import de.omnikryptec.libapi.glfw.Window;

public class WindowUpdater {

	private double swapTime = 0;
	private double fpstime = 0;
	private double deltatime = 0;
	private double lasttime = 0;
	private double frontruntime = 0;

	private long framecount = 0;

	private long fps1 = 0, fps2 = 0;
	private boolean fps = true;

	private double lastsynced;

	private Window<?> window;
	private Smoother deltaTimeSmoother;

	public WindowUpdater(Window<?> window) {
		this.window = window;
		this.deltaTimeSmoother = new Smoother();
	}

	public void update(int maxfps) {
		// TODO this is some crazy hack
		if (framecount == 0) {
			lasttime = GLFWManager.active().getTime();
			fpstime = GLFWManager.active().getTime();
		}
		double currentFrameTime = GLFWManager.active().getTime();
		deltatime = (currentFrameTime - lasttime);
		deltaTimeSmoother.push(deltatime);
		frontruntime += deltatime;
		lasttime = currentFrameTime;
		if (maxfps > 0) {
			sync(maxfps);
		}
		double tmptime = GLFWManager.active().getTime();
		window.swapBuffers();
		swapTime = GLFWManager.active().getTime() - tmptime;
		GLFWManager.active().pollEvents();
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

	private void sync(int fps) {
		double target = lastsynced + (1.0 / fps);
		try {
			while ((lastsynced = GLFWManager.active().getTime()) < target) {
				Thread.sleep(1);
			}
		} catch (InterruptedException ex) {
		}
	}

	//TODO better somewhere else?
	public Window<?> getWindow(){
		return window;
	}
	public final Smoother getDeltaTimeSmoother() {
		return deltaTimeSmoother;
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

	public double getSwapTime() {
		return swapTime;
	}

}
