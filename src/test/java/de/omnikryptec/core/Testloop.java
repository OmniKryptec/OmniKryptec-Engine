package de.omnikryptec.core;

import de.omnikryptec.core.loop.IEngineLoop;
import de.omnikryptec.graphics.display.WindowUpdater;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.updater.AbstractUpdater;

public class Testloop implements IEngineLoop {

    private WindowUpdater updater;

    @Override
    public void init(EngineLoader loader) {
        this.updater = new WindowUpdater(loader.getWindow());
    }

    @Override
    public void startLoop() {
        while (!updater.getWindow().isCloseRequested()) {
            update();
            renderAndSwap();
        }
    }

    @Override
    public void stopLoop() {
    }

    @Override
    public void update() {
    }

    @Override
    public void renderAndSwap() {
        updater.update(0);
        if (updater.getOperationCount() % 40 == 0) {
            OpenGLUtil.setClearColor(Color.randomRGB());
        }
        OpenGLUtil.clear(BufferType.COLOR);
    }

    @Override
    public boolean isRunning() {

        return false;
    }

}
