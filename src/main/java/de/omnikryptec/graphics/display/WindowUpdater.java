package de.omnikryptec.graphics.display;

import de.omnikryptec.libapi.LibAPIManager;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.util.updater.AbstractUpdater;

public class WindowUpdater extends AbstractUpdater {

    private final Window<?> window;
    private double swaptime;

    public WindowUpdater(final Window<?> window) {
        this.window = window;
    }

    @Override
    protected void operation() {
        final double time = LibAPIManager.active().getTime();
        this.window.swapBuffers();
        this.swaptime = LibAPIManager.active().getTime() - time;
        LibAPIManager.active().pollEvents();
    }

    public double getSwapTime() {
        return this.swaptime;
    }

    @Deprecated
    public Window<?> getWindow() {
        return this.window;
    }

}
