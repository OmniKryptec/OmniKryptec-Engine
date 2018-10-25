package de.omnikryptec.graphics.display;

import javax.annotation.Nonnull;

import de.omnikryptec.libapi.glfw.LibAPIManager;
import de.omnikryptec.libapi.glfw.Window;
import de.omnikryptec.util.data.Smoother;

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
	 * Updates the window maintained by this object and the values accessable by the
	 * functions of this class (e.g. {@link #getDeltaTime()}.<br>
	 * The update includes swapping the buffers and polling events.<br>
	 * <br>
	 * This function can limit the framerate by setting this Thread to sleep. This
	 * will happen if the frames per second (not counted) are greater than the
	 * specified maxfps or in other words, if idle time is available.<br>
	 * The Framerate is limited to min(vsync, maxfps), if VSync is enabled.<br>
	 * <br>
	 * This method does not clear any buffers nor does it test if the closing of the
	 * window is requested nor does it show the window.
	 * 
	 * @param maxfps limits the FPS for values greater than 0. Otherwise does
	 *               nothing.
	 */
	public void update(int maxfps) {
		// TODO this is some crazy hack
		if (framecount == 0) {
			lasttime = LibAPIManager.active().getTime();
			fpstime = LibAPIManager.active().getTime();
		}
		double currentFrameTime = LibAPIManager.active().getTime();
		deltatime = (currentFrameTime - lasttime);
		deltaTimeSmoother.push(deltatime);
		frontruntime += deltatime;
		lasttime = currentFrameTime;
		if (maxfps > 0) {
			sync(maxfps);
		}
		double tmptime = LibAPIManager.active().getTime();
		window.swapBuffers();
		swapTime = LibAPIManager.active().getTime() - tmptime;
		LibAPIManager.active().pollEvents();
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
			while ((lastsynced = LibAPIManager.active().getTime()) < target) {
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
	@Nonnull
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

//	/**
//	 * the amount of time the maintained window was in the foreground. For a
//	 * complete time since glfw initialization, see {@link GLFWManager#getTime()}.
//	 * 
//	 * @return window foreground time
//	 * @see GLFWManager#getTime()
//	 */
//	public double getFrontRunningTime() {
//		return frontruntime;
//	}

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
