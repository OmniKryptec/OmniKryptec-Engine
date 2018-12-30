package de.omnikryptec.core.scene;

import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.core.Updateable;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.util.updater.AbstractUpdater;

/**
 * A class managing the updates of a {@link GameController}.<br>
 * The {@link EngineLoader} can provide an instance.
 * 
 * @author pcfreak9000
 * @see EngineLoader#getUpdateController()
 */
public class UpdateController {
    
    private final GameController gameController;
    private final WindowUpdater windowUpdater;
    private final AbstractUpdater asyncUpdater;
    
    private int syncUpdatesPerSecond = 144;
    private int asyncUpdatesPerSecond = 144;
    
    /**
     * Creates a new {@link UpdateController}
     * 
     * @param gameController the {@link GameController} to be updated
     * @param window         the window context to render
     */
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
    
    /**
     * Updates the window and the sync {@link Updateable}s.
     * 
     * @see #setSyncUpdatesPerSecond(int)
     */
    public void updateSync() {
        this.windowUpdater.update(this.syncUpdatesPerSecond);
        this.gameController.updateSync(this.windowUpdater.asTime());
    }
    
    /**
     * Updates the async {@link Updateable}s.
     * 
     * @see #setAsyncUpdatesPerSecond(int)
     */
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
