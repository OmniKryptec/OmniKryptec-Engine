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
import de.omnikryptec.core.scene.GameController;
import de.omnikryptec.core.scene.GameController.ControllerSetting;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.util.updater.AbstractUpdater;

public class DefaultGameLoop implements IGameLoop {

    private final Runnable asyncTasks = new Runnable() {

        private AbstractUpdater updater = new AbstractUpdater();

        @Override
        public void run() {
            updater.resetDeltaTime();
            while (running.get()) {
                this.updater.update(
                        DefaultGameLoop.this.gameController.getSettings().get(ControllerSetting.UPDATES_ASYNC_PER_S));
                DefaultGameLoop.this.gameController.updateAsync(this.updater.asTime());
            }
        }
    };

    private AtomicBoolean running = new AtomicBoolean(false);
    private boolean shouldStop = false;

    private Window window;
    private WindowUpdater windowUpdater;
    private GameController gameController;

    @Override
    public void init(final EngineLoader loader) {
        this.window = loader.getWindow();
        this.windowUpdater = new WindowUpdater(this.window);
        this.gameController = loader.getController();
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
        return this.running.get();
    }

    @Override
    public void startLoop() {
        this.shouldStop = false;
        this.running.set(true);
        new Thread(asyncTasks).start();
        try {
            this.windowUpdater.resetDeltaTime();
            while (!shouldStop()) {
                this.windowUpdater.update(this.gameController.getSettings().get(ControllerSetting.UPDATES_SYNC_PER_S));
                this.gameController.updateSync(this.windowUpdater.asTime());
            }
        } finally {
            this.running.set(false);
        }
    }

}
