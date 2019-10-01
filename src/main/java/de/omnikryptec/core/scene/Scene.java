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

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.updater.Time;

/**
 * A class merging sync and async {@link Updateable}s and so form a Scene.
 *
 * @author pcfreak9000
 *
 */
public class Scene {

    //Rendering and Mainthread stuff
    private IUpdatable updateableSync;
    //Might happen on another Thread
    private IUpdatable updateableAsync;

    
    public Scene() {
        
    }
    

    
    /**
     * A simple method to add a synchronized {@link IUpdatable}. A more
     * sophisticated approach can be achieved with a {@link SceneBuilder}
     *
     * @param updt the {@link IUpdatable}
     *
     * @see #createBuilder()
     */
    public void setUpdateableSync(final IUpdatable updt) {
        this.updateableSync = updt;
    }
    
    public void setUpdateableAsync(IUpdatable updt) {
        this.updateableAsync = updt;
    }
    
    public IUpdatable getUpdateableSync() {
        return this.updateableSync;
    }
    
    public IUpdatable getUpdateableAsync() {
        return this.updateableAsync;
    }
    
    public boolean hasUpdateableSync() {
        return updateableSync != null;
    }
    
    public boolean hasUpdateableAsync() {
        return updateableAsync != null;
    }
    
    public void updateSync(Time time) {
        if (hasUpdateableSync()) {
            this.updateableSync.update(time);
        }
    }
    
    public void updateAsync(Time time) {
        if (hasUpdateableAsync()) {
            this.updateableAsync.update(time);
        }
    }

}
