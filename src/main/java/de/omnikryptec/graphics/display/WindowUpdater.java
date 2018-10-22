package de.omnikryptec.graphics.display;

import de.omnikryptec.libapi.glfw.GLFWManager;
import de.omnikryptec.libapi.glfw.Window;

/**
 * A wrapper class that is responsible to update a given {@link Window} and
 * provide various often required functions like {@link #getDeltaTime()} or
 * {@link #getFPS()}.
 * 
 * @author pcfreak9000
 *
 */
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

	/**
	 * Updates the window maintained by this object and all the corresponding values
	 * presented by this class. This includes swapping the buffers and polling
	 * events.<br>
	 * <br>
	 * This function can limit the framerate by setting this Thread to sleep. This
	 * will happen if the frames per second (not counted) are greater than the
	 * specified maxfps or in other words, if idle time is available.
	 * 
	 * @param maxfps limits the FPS for values greater than 0. Otherwise does
	 *               nothing.
	 */
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

	/**
	 * See {@link #update(int)}.
	 * 
	 * @param fps maxfps, values smaller or equal to 0 confuse this function though.
	 */
	private void sync(int fps) {
		double target = lastsynced + (1.0 / fps);
		try {
			while ((lastsynced = GLFWManager.active().getTime()) < target) {
				Thread.sleep(1);
			}
		} catch (InterruptedException ex) {
		}
	}

	// TODO better somewhere else?
	public Window<?> getWindow() {
		return window;
	}

	/**
	 * An instance of {@link Smoother} that can be used to retrieve a delta time
	 * smoothed over multiple frames, in seconds.
	 * 
	 * @return the delta time smoother
	 * @see #getDeltaTime()
	 */
	public final Smoother getDeltaTimeSmoother() {
		return deltaTimeSmoother;
	}

	/**
	 * the amount of calls to the {@link #update(int)} function since the creation
	 * of this object.
	 * 
	 * @return the frame count
	 */
	public long getFrameCount() {
		return framecount;
	}

	/**
	 * the amount of time the maintained window was in the foreground. For a
	 * complete time since glfw initialization, see {@link GLFWManager#getTime()}.
	 * 
	 * @return window foreground time
	 * @see GLFWManager#getTime()
	 */
	public double getFrontRunningTime() {
		return frontruntime;
	}

	/**
	 * the counted frames per second. Counted means that the calls to
	 * {@link #update(int)} will be counted each second, so the value of this
	 * function will only change once every second.
	 * 
	 * @return frames per second
	 */
	public long getFPS() {
		return fps ? fps2 : fps1;
	}

	/**
	 * the measured delta time. That is the elapsed time between the last and the
	 * last but one call to {@link #update(int)}, in seconds. For a smoothed value
	 * over multiple updates, see {@link #getDeltaTimeSmoother()}.
	 * 
	 * @return delta time
	 * @see #getDeltaTimeSmoother()
	 */
	public double getDeltaTime() {
		return deltatime;
	}

	/**
	 * The time spend on swapping the buffers the last time {@link #update(int)} was
	 * called, in seconds.
	 * 
	 * @return swap time
	 */
	public double getSwapTime() {
		return swapTime;
	}

}
