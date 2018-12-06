/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.core.loop;

import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.graphics.display.WindowUpdater;
import de.omnikryptec.libapi.exposed.window.Window;

public class DefaultEngineLoop implements IEngineLoop {

    private boolean running = false;
    private Window<?> window;
    private boolean shouldStop = false;

    private WindowUpdater windowUpdater;
    
    @Override
    public void init(final EngineLoader loader) {
        this.window = loader.getWindow();
        this.windowUpdater = new WindowUpdater(this.window);
    }

    @Override
    public void stopLoop() {
        this.shouldStop = true;
    }

    public boolean shouldStop() {
        return this.shouldStop || (this.window == null ? false : this.window.isCloseRequested());
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void startLoop() {
        this.shouldStop = false;
        this.running = true;
        try {
            while (!shouldStop()) {
                this.windowUpdater.update(0);
            }
        } finally {
            this.running = false;
        }
    }

}
