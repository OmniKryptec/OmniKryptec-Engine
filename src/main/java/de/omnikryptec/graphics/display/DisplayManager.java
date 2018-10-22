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

import de.omnikryptec.libapi.glfw.OpenGLWindow;
import de.omnikryptec.libapi.glfw.OpenGLWindowInfo;
import de.omnikryptec.libapi.glfw.Window;
import de.omnikryptec.libapi.glfw.WindowInfo;
import de.omnikryptec.old.settings.GameSettings;

/**
 * Display managing class
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
@Deprecated
public class DisplayManager {

	public static final int DISABLE_FPS_CAP = 0;

	private WindowUpdater displayUpdater;
	private Display display;
	
	private Window<?> window;
	
	public DisplayManager() {
		this(new GameSettings(), new OpenGLWindowInfo());
	}

	public DisplayManager(GameSettings settings, WindowInfo<?> info) {
//		this.settings = Util.ensureNonNull(settings, "GameSettings must not be null!");
		// TODO move fastmath to initializer
//		if (settings.getBoolean(GameSettings.OmnikryptecSettings.FASTMATH)) {
//			System.setProperty("joml.fastmath", "true");
//		}
		try {
			displayUpdater = new WindowUpdater(window);
//			AudioManager.init();
//			if (settings.getMultiSamples() != GameSettings.NO_MULTISAMPLING) {
//				GraphicsUtil.antialias(true);
//			}
//			// TODO opengl does not belong to this class
//			GraphicsUtil.cullBackFaces(true);
//			GraphicsUtil.enableDepthTesting(true);
//			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			// *****************************************
			System.out.println("Created the display");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private Window<?> makeWindow(WindowInfo<?> info) {
		if (info.getClass().equals(OpenGLWindowInfo.class)) {
			return new OpenGLWindow((OpenGLWindowInfo) info);
		} else {
			throw new IllegalArgumentException("Wrong window type");
		}
	}

	public final void updateDisplay() {
		int sync = 0;//settings.getInteger(GameSettings.FPS_CAP);
		displayUpdater.update(sync);
	}

	public final WindowUpdater getUpdater() {
		return displayUpdater;
	}

	public final Display getDisplay() {
		return display;
	}

}
