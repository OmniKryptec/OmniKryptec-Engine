package de.omnikryptec.core;

import de.omnikryptec.graphics.display.WindowUpdater;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.data.settings.Settings;

public class ComfortTest extends EngineLoader {

	public static void main(String[] args) {
		new ComfortTest().boot();
	}

	@Override
	protected void config(Settings<LoaderSetting> settings) {
		settings.set(LoaderSetting.DEBUG, true);
	}

	@Override
	protected void initialized() {
		WindowUpdater updater = new WindowUpdater(getWindow());
		while (!getWindow().isCloseRequested()) {
			updater.update(0);
			if (updater.getFrameCount() % 40 == 0) {
				OpenGLUtil.setClearColor(Color.randomRGB());
			}
			OpenGLUtil.clear(BufferType.COLOR);
		}
	}

}
