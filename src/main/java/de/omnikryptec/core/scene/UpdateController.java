/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.core.scene;

import java.util.function.UnaryOperator;

import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.core.Updateable;
import de.omnikryptec.libapi.exposed.window.IWindow;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.util.updater.AbstractUpdater;
import de.omnikryptec.util.updater.Time;

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
    
    private UnaryOperator<Time> syncTimeTransform = UnaryOperator.identity();
    private UnaryOperator<Time> asyncTimeTransform = UnaryOperator.identity();
    
    private int syncUpdatesPerSecond = 144;
    private int asyncUpdatesPerSecond = 144;
    
    /**
     * Creates a new {@link UpdateController}
     *
     * @param gameController the {@link GameController} to be updated
     * @param window         the window context to render
     */
    public UpdateController(final GameController gameController, final IWindow window) {
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
        this.gameController.updateSync(syncTimeTransform.apply(this.windowUpdater.asTime()));
    }
    
    /**
     * Updates the async {@link Updateable}s.
     *
     * @see #setAsyncUpdatesPerSecond(int)
     */
    public void updateAsync() {
        this.asyncUpdater.update(this.asyncUpdatesPerSecond);
        this.gameController.updateAsync(asyncTimeTransform.apply(this.asyncUpdater.asTime()));
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
    
    public void setSyncUpdateTimeTransform(UnaryOperator<Time> transform) {
        this.syncTimeTransform = transform;
    }
    
    public void setAsyncUpdateTimeTransform(UnaryOperator<Time> transform) {
        this.asyncTimeTransform = transform;
    }
    
}
