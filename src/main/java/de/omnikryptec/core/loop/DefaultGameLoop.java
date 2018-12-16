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

import java.util.concurrent.atomic.AtomicBoolean;

import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.core.scene.UpdateController;

public class DefaultGameLoop implements IGameLoop {
    
    private final Runnable asyncTasks = new Runnable() {
        
        @Override
        public void run() {
            updateController.getAsyncUpdater().resetDeltaTime();
            while (DefaultGameLoop.this.running.get()) {
                updateController.updateAsync();
            }
        }
    };
    
    private final AtomicBoolean running = new AtomicBoolean(false);
    private boolean shouldStop = false;
    
    private UpdateController updateController;
    
    @Override
    public void init(final EngineLoader loader) {
        this.updateController = loader.getUpdateController();
    }
    
    @Override
    public void stopLoop() {
        this.shouldStop = true;
    }
    
    public boolean shouldStop() {
        return this.shouldStop || (this.updateController == null ? false
                : this.updateController.getWindowUpdater().getWindow().isCloseRequested());
    }
    
    @Override
    public boolean isRunning() {
        return this.running.get();
    }
    
    @Override
    public void startLoop() {
        if (this.running.get()) {
            throw new IllegalStateException();
        }
        this.shouldStop = false;
        this.running.set(true);
        new Thread(this.asyncTasks).start();
        try {
            this.updateController.getWindowUpdater().resetDeltaTime();
            while (!shouldStop()) {
                this.updateController.updateSync();
            }
        } finally {
            this.running.set(false);
        }
    }
    
}
