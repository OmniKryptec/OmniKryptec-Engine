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

import java.util.concurrent.atomic.AtomicBoolean;

import de.omnikryptec.util.updater.Time;

public class DefaultGameLoop implements IGameLoop {
   
    
    private final AtomicBoolean running = new AtomicBoolean(false);
    private boolean shouldStop = false;
    
    private Game game;
    
    @Override
    public void setUpdateController(final Game game) {
        this.game = game;
    }
    
    @Override
    public void stopLoop() {
        this.shouldStop = true;
    }
    
    public boolean shouldStop() {
        return this.shouldStop || (this.game == null ? false
                : this.game.getWindowUpdater().getWindow().isCloseRequested());
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
        try {
            this.game.getWindowUpdater().resetDeltaTime();
            while (!shouldStop()) {
                this.game.getWindowUpdater().update(144);
                Time time = this.game.getWindowUpdater().asTime();
                game.updateGame(time);
                game.renderGame(time);
            }
        } finally {
            this.running.set(false);
        }
    }
    
}
