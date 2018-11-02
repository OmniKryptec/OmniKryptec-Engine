package de.omnikryptec.core;

import de.omnikryptec.libapi.glfw.Window;

public abstract class AbstractEngineLoop implements IEngineLoop {

    private boolean shouldStop = false;

    protected boolean running = false;
    protected Window<?> window;

    @Override
    public void init(EngineLoader loader) {
	this.window = loader.getWindow();
    }

    @Override
    public void stopLoop() {
	shouldStop = true;
    }

    public boolean shouldStop() {
	return shouldStop || (window == null ? true : window.isCloseRequested());
    }

    @Override
    public boolean isRunning() {
	return running;
    }

    @Override
    public void startLoop() {
	shouldStop = false;
	running = true;
	try {
	    while (!shouldStop()) {
		update();
		renderAndSwap();
	    }
	} finally {
	    running = false;
	}
    }

}
