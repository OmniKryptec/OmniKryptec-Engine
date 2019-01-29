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

package de.omnikryptec.core;

import de.omnikryptec.core.loop.IGameLoop;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.util.data.Color;

public class Testloop implements IGameLoop {
    
    private WindowUpdater updater;
    private Window window;

    @Override
    public void init(final EngineLoader loader) {
        this.updater = new WindowUpdater(loader.getWindow());
        this.window = loader.getWindow();
    }
    
    @Override
    public void startLoop() {
        while (!this.window.isCloseRequested()) {
            update();
            renderAndSwap();
        }
    }
    
    @Override
    public void stopLoop() {
    }
    
    public void update() {
    }
    
    public void renderAndSwap() {
        this.updater.update(0);
        if (this.updater.getOperationCount() % 40 == 0) {
            RenderAPI.get().setClearColor(Color.randomRGB());
        }
        RenderAPI.get().clear(SurfaceBuffer.Color);
    }
    
    @Override
    public boolean isRunning() {
        
        return false;
    }
    
}
