/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.core;

import java.util.function.UnaryOperator;

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.render.renderer.ViewManager;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class Scene {

    private IUpdatable gameLogic;

    private final ViewManager viewManager;
    
    private UnaryOperator<Time> timeTransform = UnaryOperator.identity();
    
    public Scene() {
        this.viewManager = new ViewManager();
    }

    public void updateScene(final Time time) {
        if (hasGameLogic()) {
            this.gameLogic.update(this.timeTransform.apply(time));
        }
    }

    public boolean hasGameLogic() {
        return this.gameLogic != null;
    }

    public void setGameLogic(final IUpdatable updatable) {
        this.gameLogic = updatable;
    }

    public IUpdatable getGameLogic() {
        return this.gameLogic;
    }

    public void setTimeTransform(UnaryOperator<Time> transform) {
        this.timeTransform = Util.ensureNonNull(transform);
    }
    
    public ViewManager getViewManager() {
        return this.viewManager;
    }

}
