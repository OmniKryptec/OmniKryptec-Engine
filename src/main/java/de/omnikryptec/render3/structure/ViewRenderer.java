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

package de.omnikryptec.render3.structure;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render3.IProjection;
import de.omnikryptec.util.updater.Time;

public interface ViewRenderer {
    
    default int priority() {
        return 0;
    }
    
    default void init(ViewManager viewManager, RenderAPI api) {
    }
    
    default void deinit(ViewManager viewManager, RenderAPI api) {
    }
    
    void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target, Time time);
}