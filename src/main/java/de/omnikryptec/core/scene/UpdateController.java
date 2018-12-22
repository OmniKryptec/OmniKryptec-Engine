package de.omnikryptec.core.scene;

import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.util.updater.AbstractUpdater;

public class UpdateController {

    private final GameController gameController;
    private final WindowUpdater windowUpdater;
    private final AbstractUpdater asyncUpdater;

    private int syncUpdatesPerSecond = 144;
    private int asyncUpdatesPerSecond = 144;

    public UpdateController(final GameController gameController, final Window window) {
        this.gameController = gameController;
        this.windowUpdater = new WindowUpdater(window);
        this.asyncUpdater = new AbstractUpdater();
    }

    public WindowUpdater getWindowUpdater() {
        return this.windowUpdater;
    }

    public AbstractUpdater getAsyncUpdater() {
        return this.asyncUpdater;
    }

    public void updateSync() {
        this.windowUpdater.update(this.syncUpdatesPerSecond);
        this.gameController.updateSync(this.windowUpdater.asTime());
    }

    public void updateAsync() {
        this.asyncUpdater.update(this.asyncUpdatesPerSecond);
        this.gameController.updateAsync(this.asyncUpdater.asTime());
    }

    public int getSyncUpdatesPerSecond() {
        return this.syncUpdatesPerSecond;
    }

    public void setSyncUpdatesPerSecond(final int syncUpdatesPerSecond) {
        this.syncUpdatesPerSecond = syncUpdatesPerSecond;
    }

    public int getAsyncUpdatesPerSecond() {
        return this.asyncUpdatesPerSecond;
    }

    public void setAsyncUpdatesPerSecond(final int asyncUpdatesPerSecond) {
        this.asyncUpdatesPerSecond = asyncUpdatesPerSecond;
    }

}
