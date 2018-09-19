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

package de.omnikryptec.core.display;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import de.omnikryptec.old.audio.AudioManager;
import de.omnikryptec.old.core.OpenCL;
import de.omnikryptec.old.graphics.GraphicsUtil;
import de.omnikryptec.old.settings.GameSettings;
import de.omnikryptec.old.util.Util;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

/**
 * Display managing class
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
public class DisplayManager {

	public static final int DISABLE_FPS_CAP = 0;

	private DisplayUpdater displayUpdater;
	private GameSettings settings;
	private Display display;
	private Smoother deltaTimeSmoother;

	public DisplayManager() {
		this(new GameSettings());
	}

	public DisplayManager(GameSettings settings) {
		this("", settings);
	}

	public DisplayManager(String name, GameSettings settings) {
		this(name, settings, new GLFWInfo());
	}

	public DisplayManager(String name, GameSettings settings, GLFWInfo info) {
		this.settings = Util.ensureNonNull(settings, "GameSettings must not be null!");
		if (settings.getBoolean(GameSettings.FASTMATH)) {
			System.setProperty("joml.fastmath", "true");
		}
		try {
			display = new Display(name, info);
			AudioManager.init();
			if (settings.getMultiSamples() != GameSettings.NO_MULTISAMPLING) {
				GraphicsUtil.antialias(true);
			}
			GraphicsUtil.cullBackFaces(true);
			GraphicsUtil.enableDepthTesting(true);
			// TODO
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			deltaTimeSmoother = new Smoother();
			Logger.log("Successfully created the Display!", LogLevel.FINEST);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public final void updateDisplay() {
		displayUpdater.update(display);
		deltaTimeSmoother.push(displayUpdater.getDeltaTime());
		int sync = settings.getInteger(GameSettings.FPS_CAP);
		if (sync != DISABLE_FPS_CAP) {
			display.sync(sync);
		}
	}

	public final GameSettings getSettings() {
		return settings;
	}

	public final DisplayUpdater getUpdater() {
		return displayUpdater;
	}
	
	public final Display getDisplay() {
		return display;
	}
	
	public final Smoother getDeltaTimeSmoother() {
		return deltaTimeSmoother;
	}
	
	public final DisplayManager close() {
		AudioManager.cleanup();
		OpenCL.cleanup();
		display.destroy();
		return this;
	}
}
