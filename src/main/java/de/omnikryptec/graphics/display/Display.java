/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.graphics.display;

import org.lwjgl.glfw.GLFW;

import de.omnikryptec.core.StateManager;
import de.omnikryptec.old.event.input.InputManager;
import de.omnikryptec.old.graphics.OpenGL;

public class Display {

	private Window window;
	private double lastsynced;
	private int[] viewport = new int[4];
	private double aspectratio = -1;

	Display(String name, Window window) {
		this.window = window;
		calcViewport();
		setARViewPort();
		// TODO Eventbased? / Input has nothing to do with the Display, move it to a different position
		InputManager.initCallbacks();
		lastsynced = getCurrentTime();
	}

	public boolean shouldBeFullscreen() {
		return window.shouldBeFullscreen();
	}

	public boolean wasResized() {
		return window.wasResized();
	}

	void update() {
		window.swapBuffers();
		StateManager.pollEvents();
		if (wasResized()) {
			calcViewport();
			setARViewPort();
		}
	}

	void destroy() {
		window.dispose();
	}

	@Deprecated
	final double getCurrentTime() {
		return GLFW.glfwGetTime() * 1000;
	}

	void sync(int fps) {
		double target = lastsynced + (1000.0 / fps);
		try {
			while ((lastsynced = getCurrentTime()) < target) {
				Thread.sleep(1);
			}
		} catch (InterruptedException ex) {
		}
	}

	public boolean isCloseRequested() {
		return window.isCloseRequested();
	}

	public boolean isActive() {
		return window.isActive();
	}

	public int getWidth() {
		// return getBufferWidth();
		return viewport[2];
	}

	public int getHeight() {
		// return getBufferHeight();
		return viewport[3];
	}

	public int getBufferWidth() {
		return window.getBufferWidth();
	}

	public int getBufferHeight() {
		return window.getBufferHeight();
	}

	public final long getID() {
		return window.getWindowID();
	}

	public final void show() {
		window.show();
	}

	public final void resetViewport() {
		OpenGL.gl11viewport(0, 0, getBufferWidth(), getBufferHeight());
	}

	public final void setARViewPort() {
		// resetViewport();
		OpenGL.gl11viewport(viewport);
	}

	public final void resetAspectRatio() {
		setAspectRatio(-1);
	}

	public final int[] calculateViewport(int w, int h) {
		int[] viewport = new int[4];
		viewport[0] = 0;
		viewport[1] = 0;
		viewport[2] = w;
		viewport[3] = h;
		if (aspectratio > 0) {
			if ((double) w / (double) h <= aspectratio) {
				viewport[3] = (int) (w * (1.0 / aspectratio));
				viewport[1] = (int) ((h - viewport[3]) * 0.5);
			} else {
				viewport[2] = (int) (h * aspectratio);
				viewport[0] = (int) ((w - viewport[2]) * 0.5);
			}
		}
		return viewport;
	}

	private final void calcViewport() {
		viewport = calculateViewport(getBufferWidth(), getBufferHeight());
	}

	public double getAspectRatio() {
		return aspectratio;
	}

	public int[] getViewportData() {
		return viewport;
	}

	public final void setAspectRatio(double a) {
		aspectratio = a;
		calcViewport();
		setARViewPort();
	}

}
