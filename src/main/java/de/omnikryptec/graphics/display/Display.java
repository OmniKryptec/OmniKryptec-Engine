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

import de.omnikryptec.libapi.glfw.GLFWManager;
import de.omnikryptec.libapi.glfw.Window;
import de.omnikryptec.old.event.input.InputManager;
import de.omnikryptec.old.graphics.OpenGL;
import de.omnikryptec.util.Maths;

//TODO unneccessary: just wrapping Window and doing weird stuff here, viewport stuff might be useful though
public class Display {

	private Window<?> window;
	private double lastsynced;
	private int[] viewport = new int[4];
	private double aspectratio = -1;

	Display(Window<?> window) {
		this.window = window;
		calcViewport();
		setARViewPort();
		// TODO Eventbased? / Input has nothing to do with the Display, move it to a
		// different position
		InputManager.initCallbacks();
		lastsynced = GLFWManager.active().getTime();
	}

	void update() {
		window.swapBuffers();
		GLFWManager.active().pollEvents();
		if (window.wasResized()) {
			calcViewport();
			setARViewPort();
		}
	}

	void sync(int fps) {
		double target = lastsynced + (1000.0 / fps);
		try {
			while ((lastsynced = GLFWManager.active().getTime()) < target) {
				Thread.sleep(1);
			}
		} catch (InterruptedException ex) {
		}
	}

	public int getWidth() {
		return viewport[2];
	}

	public int getHeight() {
		return viewport[3];
	}

	public final Window<?> getWindow() {
		return window;
	}

	// TODO opengl does not belong to this class
	// *********************************************************/
	public final void resetViewport() {
		OpenGL.gl11viewport(0, 0, window.getBufferWidth(), window.getBufferHeight());
	}

	public final void setARViewPort() {
		OpenGL.gl11viewport(viewport);
	}
	// *********************************************************/

	public final void resetAspectRatio() {
		setAspectRatio(-1);
	}

	private final void calcViewport() {
		viewport = Maths.calculateViewport(aspectratio, window.getBufferWidth(), window.getBufferHeight());
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
