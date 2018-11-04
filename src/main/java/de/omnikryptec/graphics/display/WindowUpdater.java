package de.omnikryptec.graphics.display;

import de.omnikryptec.libapi.glfw.LibAPIManager;
import de.omnikryptec.libapi.glfw.window.Window;
import de.omnikryptec.util.updater.AbstractUpdater;

public class WindowUpdater extends AbstractUpdater {

    private Window<?> window;
    private double swaptime;

    public WindowUpdater(Window<?> window) {
        this.window = window;
    }

    @Override
    protected void operation() {
        double time = LibAPIManager.active().getTime();
        window.swapBuffers();
        swaptime = LibAPIManager.active().getTime() - time;
        LibAPIManager.active().pollEvents();
    }

    public double getSwapTime() {
        return swaptime;
    }

    @Deprecated
    public Window<?> getWindow() {
        return window;
    }

}
