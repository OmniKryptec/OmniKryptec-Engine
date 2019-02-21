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

import de.omnikryptec.core.loop.DefaultGameLoop;
import de.omnikryptec.core.loop.IGameLoop;
import de.omnikryptec.core.scene.GameController;
import de.omnikryptec.core.scene.UpdateController;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.resource.loadervpc.ResourceManager;
import de.omnikryptec.resource.loadervpc.ResourceProvider;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

import javax.annotation.Nonnull;

/**
 * The application entry point of the OmniKryptec-Engine. Can be used static and
 * non-static.<br>
 * <ul>
 * <li>In the non-static case, the engine will be started via
 * {@link de.omnikryptec.core.EngineLoader#start()}.</li>
 * <li>In the static case, this class only provides utility functions.</li>
 * </ul>
 * The use of this class is not a requirement to use the Engine; libraries and
 * parts of the Engine can be initialized/used independently.
 *
 * @author pcfreak9000 &amp; Panzer1119
 */
public abstract class EngineLoader {
    
    private IGameLoop gameLoop;
    private Window window;
    private GameController gameController;
    private UpdateController updateController;
    private ResourceManager resources;
    private boolean started;
    
    public EngineLoader() {
    }
    
    /**
     * Initializes various parts of the {@link de.omnikryptec.libapi.exposed.LibAPIManager}.
     *
     * @param libSettings Settings configuring the {@link de.omnikryptec.libapi.exposed.LibAPIManager}
     * @param rendererApi the RenderAPI to use
     * @param apiSettings Settings of the RenderAPI (this is RenderAPI specific)
     */
    public static void initialize(final Settings<LibSetting> libSettings, final Class<? extends RenderAPI> rendererApi, final Settings<IntegerKey> apiSettings) {
        // Initialize everything required
        LibAPIManager.init(libSettings);
        LibAPIManager.instance().setRenderer(rendererApi, apiSettings);
        //TODO Audio, etc....
    }
    
    /**
     * Starts the engine. The start parameters can be set in
     * {@link de.omnikryptec.core.EngineLoader#configure(Settings, Settings, Settings, Settings)}. <br>
     * Then the engine gets {@link de.omnikryptec.core.EngineLoader#initialize(Settings, Class, Settings)} and the
     * {@link de.omnikryptec.libapi.exposed.window.Window}, {@link de.omnikryptec.core.loop.IGameLoop}, {@link de.omnikryptec.core.scene.GameController} and
     * {@link de.omnikryptec.core.scene.UpdateController} are created.<br>
     * After or before making the window visible, {@link de.omnikryptec.core.EngineLoader#onInitialized()} is
     * called. If so configured, the {@link de.omnikryptec.core.loop.IGameLoop} will be started.
     */
    @Nonnull
    public void start() {
        if (this.started) {
            throw new IllegalStateException("Was already booted");
        }
        final Settings<LoaderSetting> loaderSettings = new Settings<>();
        final Settings<LibSetting> libSettings = new Settings<>();
        final Settings<WindowSetting> windowSettings = new Settings<>();
        final Settings<IntegerKey> rapiSettings = new Settings<>();
        configure(loaderSettings, libSettings, windowSettings, rapiSettings);
        // or let them (the natives) be loaded by Configuration.SHARED_LIBRARY and
        // LIBRARY_PATH <-- Seems to work, so better use it
        initialize(libSettings, loaderSettings.get(LoaderSetting.RENDER_API), rapiSettings);
        this.resources = new ResourceManager();
        this.resources.addDefaultLoader();
        this.window = LibAPIManager.instance().getRenderAPI().createWindow(windowSettings);
        this.gameLoop = loaderSettings.get(LoaderSetting.GAME_LOOP);
        this.gameController = new GameController();
        this.updateController = new UpdateController(this.gameController, this.window);
        this.started = true;
        if (loaderSettings.get(LoaderSetting.SHOW_WINDOW_AFTER_CREATION) == WindowMakeVisible.IMMEDIATELY) {
            this.window.setVisible(true);
        }
        onInitialized();
        if (loaderSettings.get(LoaderSetting.SHOW_WINDOW_AFTER_CREATION) == WindowMakeVisible.AFTER_INIT) {
            this.window.setVisible(true);
        }
        if (this.gameLoop != null) {
            this.gameLoop.init(this);
            if ((boolean) loaderSettings.get(LoaderSetting.START_GAME_LOOP_AFTER_INIT)) {
                this.gameLoop.startLoop();
                if ((boolean) loaderSettings.get(LoaderSetting.SHUTDOWN_ON_LOOP_EXIT)) {
                    shutdown();
                }
            }
        }
    }
    
    /**
     * Shuts down the engine. Only if it has been started by {@link de.omnikryptec.core.EngineLoader#start()}.<br>
     * First {@link de.omnikryptec.core.EngineLoader#onShutdown()} gets called. A {@link de.omnikryptec.core.loop.IGameLoop} that might be running
     * gets stopped, the window gets disposed and the {@link de.omnikryptec.libapi.exposed.LibAPIManager} gets shut down.
     */
    public void shutdown() {
        if (this.started) {
            onShutdown();
            if (this.gameLoop != null) {
                this.gameLoop.stopLoop();
            }
            // Shutdown, etc...
            this.window.dispose();
            this.started = false;
            LibAPIManager.shutdown();
        }
    }
    
    public Window getWindow() {
        checkStarted();
        return this.window;
    }
    
    public IGameLoop getEngineLoop() {
        checkStarted();
        return this.gameLoop;
    }
    
    public GameController getGameController() {
        checkStarted();
        return this.gameController;
    }
    
    public UpdateController getUpdateController() {
        checkStarted();
        return this.updateController;
    }
    
    public ResourceManager getResManager() {
        return this.resources;
    }
    
    //Shortcut
    public ResourceProvider getResProvider() {
        return this.resources.getProvider();
    }
    
    private void checkStarted() {
        if (!this.started) {
            throw new IllegalStateException(getClass().getSimpleName() + " has not been started yet");
        }
    }
    
    public void switchGameloop(final IGameLoop newloop) {
        Util.ensureNonNull(newloop);
        final boolean running = this.gameLoop.isRunning();
        if (running) {
            this.gameLoop.stopLoop();
        }
        this.gameLoop = newloop;
        if (running) {
            this.gameLoop.startLoop();
        }
    }
    
    public ResourceManager setResourceManager(final ResourceManager proc) {
        final ResourceManager old = this.resources;
        this.resources = proc;
        return old;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    protected void configure(final Settings<LoaderSetting> loaderSettings, final Settings<LibSetting> libSettings, final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apiSettings) {
    }
    
    protected abstract void onInitialized();
    
    protected void onShutdown() {
    }
    
    public enum LoaderSetting implements Defaultable {
        
        /**
         * The rendering API to use by the engine. Only in non-static cases of
         * {@link de.omnikryptec.core.EngineLoader}.<br>
         * <br>
         * The default value is {@link de.omnikryptec.libapi.exposed.render.RenderAPI#OpenGL}.
         */
        RENDER_API(RenderAPI.OpenGL),
        /**
         * When to show the window after it's creation. Only in non-static cases of
         * {@link de.omnikryptec.core.EngineLoader}. <br>
         * <br>
         * The default value is {@link de.omnikryptec.core.EngineLoader.WindowMakeVisible#IMMEDIATELY}.
         *
         * @see WindowMakeVisible
         */
        SHOW_WINDOW_AFTER_CREATION(WindowMakeVisible.IMMEDIATELY),
        /**
         * The option that defines if the {@link de.omnikryptec.core.loop.IGameLoop} should be started after
         * initialization. Only in non-static cases of {@link de.omnikryptec.core.EngineLoader} and only for
         * non-null {@link de.omnikryptec.core.EngineLoader.LoaderSetting#GAME_LOOP}.<br>
         * <br>
         * The default value is <code>true</code>
         */
        START_GAME_LOOP_AFTER_INIT(true),
        /**
         * The game-loop that might be started after initialization. Only in non-static
         * cases of {@link de.omnikryptec.core.EngineLoader}<br>
         * <br>
         * The default value is {@link de.omnikryptec.core.loop.DefaultGameLoop}
         *
         * @see de.omnikryptec.core.EngineLoader.LoaderSetting#START_GAME_LOOP_AFTER_INIT
         */
        GAME_LOOP(new DefaultGameLoop()),
        
        /**
         * Shutdown the engine if the {@link de.omnikryptec.core.loop.IGameLoop} exits. Only if
         * {@link de.omnikryptec.core.EngineLoader.LoaderSetting#START_GAME_LOOP_AFTER_INIT} is set to <code>true</code>. Only in
         * non-static cases of {@link de.omnikryptec.core.EngineLoader}<br>
         * <br>
         * The default value is <code>true</code>
         */
        SHUTDOWN_ON_LOOP_EXIT(true);
        
        private final Object defaultSetting;
        
        LoaderSetting(final Object defaultSetting) {
            this.defaultSetting = defaultSetting;
        }
        
        @Override
        public <T> T getDefault() {
            return (T) this.defaultSetting;
        }
    }
    
    /**
     * Will only be used in non-static cases of {@link de.omnikryptec.core.EngineLoader}. Defines when
     * to show the {@link de.omnikryptec.libapi.exposed.window.Window}.
     *
     * @author pcfreak9000
     * @see de.omnikryptec.libapi.exposed.window.Window#setVisible(boolean)
     */
    public enum WindowMakeVisible {
        
        /**
         * Show the window immediately after creation, and before
         * {@link de.omnikryptec.core.EngineLoader#onContextCreationFinish()}.
         */
        IMMEDIATELY,
        /**
         * Show the window after {@link de.omnikryptec.core.EngineLoader#onContextCreationFinish()}.
         */
        AFTER_INIT,
        /**
         * Never show the window.
         */
        NEVER
        
    }
    
}
