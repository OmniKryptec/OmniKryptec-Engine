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

package de.omnikryptec.core;

import de.omnikryptec.libapi.glfw.Window;

public abstract class AbstractEngineLoop implements IEngineLoop {
    
    protected boolean running = false;
    protected Window<?> window;
    private boolean shouldStop = false;
    
    @Override
    public void init(EngineLoader loader) {
        this.window = loader.getWindow();
    }
    
    @Override
    public void stopLoop() {
        shouldStop = true;
    }
    
    public boolean shouldStop() {
        return shouldStop || (window == null ? false : window.isCloseRequested());
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public void startLoop() {
        shouldStop = false;
        running = true;
        try {
            while (!shouldStop()) {
                update();
                renderAndSwap();
            }
        } finally {
            running = false;
        }
    }
    
}
