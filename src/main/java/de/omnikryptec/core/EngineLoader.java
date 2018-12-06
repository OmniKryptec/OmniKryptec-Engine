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

import javax.annotation.Nonnull;

import de.omnikryptec.core.loop.DefaultEngineLoop;
import de.omnikryptec.core.loop.IEngineLoop;
import de.omnikryptec.libapi.LibAPIManager;
import de.omnikryptec.libapi.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.OpenGLWindowInfo;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.WindowInfo;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.Settings;

/**
 * The application entry point of the Omnikryptec-Engine. Can be used static and
 * non-static.<br>
 * <ul>
 * <li>In the non-static case, the engine will be started via
 * {@link #boot()}.</li>
 * <li>In the static case, this class only provides utility functions.</li>
 * </ul>
 * The use of this class is not a requirement to use the Engine; librarys and
 * parts of the Engine can be initialized/used independently.
 *
 * @author pcfreak9000
 */
public abstract class EngineLoader {

    private IEngineLoop engineLoop;
    private Window<?> window;
    private boolean booted;

    public EngineLoader() {
    }

    public static void initialize() {
        // Initialize everything required
        LibAPIManager.init();
        // Audio, etc....
    }

    @Nonnull
    public EngineLoader boot() {
        if (this.booted) {
            throw new IllegalStateException("Was already booted");
        }
        final Settings<LoaderSetting> loaderSettings = new Settings<>();
        final Settings<LibSetting> libSettings = new Settings<>();
        config(loaderSettings, libSettings);
        // or let them (the natives) be loaded by Configuration.SHARED_LIBRARY and
        // LIBRARY_PATH <-- Seems to work, so better use it
        initialize();
        this.window = ((WindowInfo<?>) loaderSettings.get(LoaderSetting.WINDOW_INFO)).createWindow();
        this.engineLoop = loaderSettings.get(LoaderSetting.ENGINE_LOOP);
        this.booted = true;
        if (loaderSettings.get(LoaderSetting.SHOW_WINDOW_AFTER_CREATION) == WindowMakeVisible.IMMEDIATELY) {
            this.window.setVisible(true);
        }
        onContextCreationFinish();
        if (loaderSettings.get(LoaderSetting.SHOW_WINDOW_AFTER_CREATION) == WindowMakeVisible.AFTERINIT) {
            this.window.setVisible(true);
        }
        onInitialized();
        if (this.engineLoop != null) {
            this.engineLoop.init(this);
            if ((boolean) loaderSettings.get(LoaderSetting.START_ENGINE_LOOP_AFTER_INIT)) {
                this.engineLoop.startLoop();
            }
        }
        return this;
    }

    public void shutdown() {
        onShutdown();
        if (this.engineLoop != null) {
            this.engineLoop.stopLoop();
        }
        // Shutdown, etc...
        this.window.dispose();
        LibAPIManager.shutdown();
    }

    public Window<?> getWindow() {
        if (!this.booted) {
            throw new IllegalStateException("Window is not created yet");
        }
        return this.window;
    }

    public IEngineLoop getEngineLoop() {
        return this.engineLoop;
    }

    public void switchGameloop(final IEngineLoop newloop) {
        Util.ensureNonNull(newloop);
        final boolean running = this.engineLoop.isRunning();
        if (running) {
            this.engineLoop.stopLoop();
        }
        this.engineLoop = newloop;
        if (running) {
            this.engineLoop.startLoop();
        }
    }

    public boolean isBooted() {
        return this.booted;
    }

    protected void config(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings) {
    }

    protected abstract void onContextCreationFinish();

    protected void onShutdown() {
    }

    protected void onInitialized() {
    }

    public enum LoaderSetting implements Defaultable {

        /**
         * The window-/contextcreation information. Only in non-static cases of
         * {@link EngineLoader}.<br>
         * <br>
         * The default value is a default {@link OpenGLWindowInfo}.
         *
         * @see WindowInfo
         */
        WINDOW_INFO(new OpenGLWindowInfo()),
        /**
         * When to show the window after it's creation. Only in non-static cases of
         * {@link EngineLoader}. <br>
         * <br>
         * The default value is {@link WindowMakeVisible#IMMEDIATELY}.
         *
         * @see WindowMakeVisible
         */
        SHOW_WINDOW_AFTER_CREATION(WindowMakeVisible.IMMEDIATELY),
        /**
         * The option that defines if the gameloop should be started after
         * initialization. Only in non-static cases of {@link EngineLoader} and only for
         * non-null {@link #ENGINE_LOOP}.<br>
         * <br>
         * The default value is <code>true</code>
         */
        START_ENGINE_LOOP_AFTER_INIT(true),
        /**
         * The game-loop that might be started after initialization. Only in non-static
         * cases of {@link EngineLoader}<br>
         * <br>
         * The default value is {@link DefaultEngineLoop}
         *
         * @see #START_ENGINE_LOOP_AFTER_INIT
         */
        ENGINE_LOOP(new DefaultEngineLoop());

        private final Object defaultSetting;

        LoaderSetting(final Object def) {
            this.defaultSetting = def;
        }

        @Override
        public <T> T getDefault() {
            return (T) this.defaultSetting;
        }
    }

    /**
     * Will only be used in non-static cases of {@link EngineLoader}. Defines when
     * to show the {@link Window}.
     *
     * @author pcfreak9000
     */
    public enum WindowMakeVisible {
        /**
         * Show the window immediately after creation, and before
         * {@link EngineLoader#onContextCreationFinish()}.
         */
        IMMEDIATELY,
        /**
         * Show the window after {@link EngineLoader#onContextCreationFinish()}.
         */
        AFTERINIT,
        /**
         * Never show the window.
         */
        NEVER
    }

}
