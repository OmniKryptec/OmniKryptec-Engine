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

package de.omnikryptec.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.gui.GuiManager;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.render3.structure.RenderManager;
import de.omnikryptec.render3.structure.ViewManager;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.updater.Time;

public class Game {
    
    private static class PrioScene {
        Scene scene;
        int prio;
    }
    
    private static final Comparator<PrioScene> SCENE_PRIORITY_COMPARATOR = (e1, e2) -> e2.prio - e1.prio;
    
    private final List<PrioScene> scenes;
    
    private final InputManager input;
    
    private final RenderManager renderManager;
    
    private final GuiManager guiManager;
    
    private EventBus eventBus;
    
    private final WindowUpdater windowUpdater;
    
    private boolean enableRenderContext = true;
    
    private UnaryOperator<Time> timeTransform = UnaryOperator.identity();
    
    public Game(final KeySettings keySettings) {
        this.scenes = new ArrayList<>();
        this.input = new InputManager(keySettings);
        this.renderManager = new RenderManager();
        ViewManager guiViewManager = new ViewManager();
        this.renderManager.addViewManager(guiViewManager, 100);
        this.guiManager = new GuiManager(guiViewManager);
        this.windowUpdater = new WindowUpdater(LibAPIManager.instance().getGLFW().getRenderAPI().getWindow());
        this.windowUpdater.update();
        this.eventBus = new EventBus(false);
    }
    
    public Scene createAndAddScene() {
        return createAndAddScene(0);
    }
    
    public Scene createAndAddScene(int prio) {
        final Scene newScene = new Scene();
        addScene(newScene, prio);
        return newScene;
    }
    
    public GuiManager getGuiManager() {
        return this.guiManager;
    }
    
    public void prepareGame(final Time time) {
        this.input.update(time);
    }
    
    public void updateGame(final Time time) {
        for (int i = this.scenes.size() - 1; i >= 0; i--) {
            this.scenes.get(i).scene.updateScene(this.timeTransform.apply(time));
            this.eventBus.processQueuedEvents();
        }
    }
    
    public void renderGame(final Time time) {
        if (this.enableRenderContext) {
            this.renderManager.renderAll(time);
        }
    }
    
    public WindowUpdater getWindowUpdater() {
        return this.windowUpdater;
    }
    
    public void setDefaultRenderingEnabled(final boolean b) {
        this.enableRenderContext = b;
    }
    
    public boolean getDefaultRenderingEnabled() {
        return this.enableRenderContext;
    }
    
    public void addScene(Scene scene) {
        addScene(scene, 0);
    }
    
    public void addScene(Scene scene, int prio) {
        PrioScene prioScene = new PrioScene();
        prioScene.scene = scene;
        prioScene.prio = prio;
        this.scenes.add(prioScene);
        this.renderManager.addViewManager(scene.getViewManager(), prio);
        this.scenes.sort(SCENE_PRIORITY_COMPARATOR);
    }
    
    public void removeScene(final Scene scene) {
        this.renderManager.removeViewManager(scene.getViewManager());
        this.scenes.removeIf((e) -> e.scene == scene);
    }
    
    public InputManager getInput() {
        return this.input;
    }
    
    public void setEventBus(final EventBus bus) {
        this.eventBus = bus;
    }
    
    public EventBus getEventBus() {
        return this.eventBus;
    }
    
    public void setTimeTransform(UnaryOperator<Time> transform) {
        this.timeTransform = Util.ensureNonNull(transform);
    }
    
    //TODO pcfreak9000 improve (selective updating?)
    public void updateAll() {
        this.getWindowUpdater().update();
        final Time time = this.getWindowUpdater().asTime();
        this.prepareGame(time);
        this.updateGame(time);
        this.renderGame(time);
    }
    
}
