package de.omnikryptec.core;

import de.omnikryptec.core.loop.IEngineLoop;
import de.omnikryptec.graphics.display.WindowUpdater;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;

public class Testloop implements IEngineLoop {

    private WindowUpdater updater;

    @Override
    public void init(final EngineLoader loader) {
        this.updater = new WindowUpdater(loader.getWindow());
    }

    @Override
    public void startLoop() {
        while (!this.updater.getWindow().isCloseRequested()) {
            update();
            renderAndSwap();
        }
    }

    @Override
    public void stopLoop() {
    }

    public void update() {
    }

    public void renderAndSwap() {
        this.updater.update(0);
        if (this.updater.getOperationCount() % 40 == 0) {
            OpenGLUtil.setClearColor(Color.randomRGB());
        }
        OpenGLUtil.clear(BufferType.COLOR);
    }

    @Override
    public boolean isRunning() {

        return false;
    }

}
