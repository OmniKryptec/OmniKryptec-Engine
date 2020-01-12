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
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.render.renderer.RendererContext;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.updater.Time;

public class Game {
    private static final Comparator<Scene> SCENE_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority() - e1.priority();
    
    private final List<Scene> scenes;
    
    private final InputManager input;
    
    private final RendererContext rendererContext;
    
    private final GuiManager guiManager;
    
    private EventBus eventBus;
    
    private final WindowUpdater windowUpdater;
    
    private boolean enableRenderContext = true;
    
    private UnaryOperator<Time> timeTransform = UnaryOperator.identity();
    
    public Game(final KeySettings keySettings) {
        this.scenes = new ArrayList<>();
        this.input = new InputManager(keySettings);
        this.rendererContext = new RendererContext();
        LocalRendererContext lrcGui = this.rendererContext.createLocal();
        lrcGui.setPriority(100);
        this.rendererContext.addLocal(lrcGui);
        this.guiManager = new GuiManager(lrcGui);
        this.windowUpdater = new WindowUpdater(this.rendererContext.getRenderAPI().getWindow());
        this.eventBus = new EventBus(false);
    }
    
    public Scene createNewScene(boolean add) {
        final Scene newScene = new Scene(this.rendererContext.createLocal(null, 0), this, 0);
        if (add) {
            addScene(newScene);
        }
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
            this.scenes.get(i).updateScene(timeTransform.apply(time));
            this.eventBus.processQueuedEvents();
        }
    }
    
    public void renderGame(final Time time) {
        if (this.enableRenderContext) {
            this.rendererContext.renderComplete(time);
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
        this.scenes.add(scene);
        this.rendererContext.addLocal(scene.getRendering());
        notifyPriorityChange();
    }
    
    public void removeScene(final Scene scene) {
        this.rendererContext.removeLocal(scene.getRendering());
        this.scenes.remove(scene);
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
        timeTransform = Util.ensureNonNull(transform);
    }
    
    void notifyPriorityChange() {
        this.scenes.sort(SCENE_PRIORITY_COMPARATOR);
    }
    
}
