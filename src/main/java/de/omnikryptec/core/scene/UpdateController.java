package de.omnikryptec.core.scene;

import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.util.updater.AbstractUpdater;

public class UpdateController {
    
    private GameController gameController;
    private WindowUpdater windowUpdater;
    private AbstractUpdater asyncUpdater;
    
    private int syncUpdatesPerSecond = 144;
    private int asyncUpdatesPerSecond = 144;
    
    public UpdateController(GameController gameController, Window window) {
        this.gameController = gameController;
        this.windowUpdater = new WindowUpdater(window);
        this.asyncUpdater = new AbstractUpdater();
    }
    
    public WindowUpdater getWindowUpdater() {
        return windowUpdater;
    }
    
    public AbstractUpdater getAsyncUpdater() {
        return asyncUpdater;
    }
    
    public void updateSync() {
        windowUpdater.update(syncUpdatesPerSecond);
        gameController.updateSync(windowUpdater.asTime());
    }
    
    public void updateAsync() {
        asyncUpdater.update(asyncUpdatesPerSecond);
        gameController.updateAsync(asyncUpdater.asTime());
    }
    
    public int getSyncUpdatesPerSecond() {
        return syncUpdatesPerSecond;
    }
    
    public void setSyncUpdatesPerSecond(int syncUpdatesPerSecond) {
        this.syncUpdatesPerSecond = syncUpdatesPerSecond;
    }
    
    public int getAsyncUpdatesPerSecond() {
        return asyncUpdatesPerSecond;
    }
    
    public void setAsyncUpdatesPerSecond(int asyncUpdatesPerSecond) {
        this.asyncUpdatesPerSecond = asyncUpdatesPerSecond;
    }
    
}
