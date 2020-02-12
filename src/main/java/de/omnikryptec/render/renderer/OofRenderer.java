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

package de.omnikryptec.render.renderer;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.updater.Time;
@Deprecated
public interface OofRenderer {
    
    void init(LocalRendererContext context, FrameBuffer target);
    
    default void preRender(final Time time, final IProjection projection, final LocalRendererContext context) {
    }
    
    void render(Time time, IProjection projection, LocalRendererContext context);
    
    default void postRender(final Time time, final IProjection projection, final LocalRendererContext context) {
    }
    
    default void resizeFBOs(final LocalRendererContext context, final SurfaceBuffer screen) {
    }
    
    void deinit(LocalRendererContext context);
    
    default int priority() {
        return 0;
    }
}
