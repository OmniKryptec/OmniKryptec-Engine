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

import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.util.updater.Time;

/**
 * A class bundling various scenes.<br>
 * The {@link EngineLoader} can provide an instance.
 *
 * @author pcfreak9000
 * @see EngineLoader#getGameController()
 */
public class GameController {

    private final Scene globalScene;
    private Scene localScene;

    public GameController() {
        this.globalScene = new Scene();
    }

    /**
     * A global, omni-present {@link Scene}
     *
     * @return the global scene
     */
    public Scene getGlobalScene() {
        return this.globalScene;
    }

    /**
     * THe currently set local {@link Scene}
     *
     * @return the local scene
     */
    public Scene getLocalScene() {
        return this.localScene;
    }

    /**
     * If a local {@link Scene} is set
     *
     * @return true if a local scene is set
     */
    public boolean hasScene() {
        return this.localScene != null;
    }

    /**
     * Sets the local {@link Scene}
     *
     * @param scene the new local scene
     */
    public void setLocalScene(final Scene scene) {
        this.localScene = scene;
    }

    /**
     * Updates the sync parts of the global and local scene.<br>
     * First, the global scene gets pre-updated. If a local scene is set, it gets
     * updated. Then the normal- and post-update of the global scene are called.
     *
     * @param time the time data
     */
    public void updateSync(final Time time) {
        getGlobalScene().getUpdateableContainerSync().preUpdate(time);
        if (hasScene()) {
            getLocalScene().getUpdateableContainerSync().preUpdate(time);
            getLocalScene().getUpdateableContainerSync().update(time);
            getLocalScene().getUpdateableContainerSync().postUpdate(time);
        }
        getGlobalScene().getUpdateableContainerSync().update(time);
        getGlobalScene().getUpdateableContainerSync().postUpdate(time);
    }

    /**
     * Updates the async parts of the global and local scene.<br>
     * First, the global scene gets pre-updated. If a local scene is set, it gets
     * updated. Then the normal- and post-updadate are executed.
     *
     * @param time the time data
     */
    public void updateAsync(final Time time) {
        getGlobalScene().getUpdateableContainerAsync().preUpdate(time);
        if (hasScene()) {
            getLocalScene().getUpdateableContainerAsync().preUpdate(time);
            getLocalScene().getUpdateableContainerAsync().update(time);
            getLocalScene().getUpdateableContainerAsync().postUpdate(time);
        }
        getGlobalScene().getUpdateableContainerAsync().update(time);
        getGlobalScene().getUpdateableContainerAsync().postUpdate(time);
    }
}
