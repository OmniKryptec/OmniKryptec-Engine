package de.omnikryptec.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.gui.GuiManager;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.render.renderer.RendererContext;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.updater.Time;

public class Game {
    private static final Comparator<Scene> SCENE_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority() - e1.priority();
    
    private final List<Scene> scenes;
    
    private final InputManager input;
    
    private final RendererContext rendererContext;
    
    private EventBus eventBus;
    
    private final WindowUpdater windowUpdater;
    
    private boolean enableRenderContext = true;
    
    public Game(final KeySettings keySettings) {
        this.scenes = new ArrayList<>();
        this.input = new InputManager(keySettings);
        this.rendererContext = new RendererContext();
        this.windowUpdater = new WindowUpdater(this.rendererContext.getRenderAPI().getWindow());
        this.eventBus = new EventBus(false);
    }
    
    //FIXME mirror added/removed scenes in the rendercontextmanager stuff so scenes that are not added do NOT get rendered
    
    public Scene createNewScene() {
        final Scene newScene = new Scene(this.rendererContext.createLocal(), this, 0);
        this.scenes.add(newScene);
        notifyPriorityChange();
        return newScene;
    }
    
    @Deprecated //TODO better use one GuiManager for one instance of Game?
    public GuiManager createNewGuiManager() {
        return new GuiManager(this.rendererContext.createLocal());
    }
    
    //*******************************************
    
    public void prepareGame(final Time time) {
        this.input.update(time);
    }
    
    public void updateGame(final Time time) {
        for (int i = this.scenes.size() - 1; i >= 0; i--) {
            this.scenes.get(i).updateScene(time);
            this.eventBus.processQueuedEvents();
        }
    }
    
    public void renderGame(final Time time) {
        if (this.enableRenderContext) {
            this.rendererContext.renderComplete(time);
        }
    }
    
    //*******************************************
    
    public WindowUpdater getWindowUpdater() {
        return this.windowUpdater;
    }
    
    public void setDefaultRenderingEnabled(final boolean b) {
        this.enableRenderContext = b;
    }
    
    public boolean getDefaultRenderingEnabled() {
        return this.enableRenderContext;
    }
    
    public void removeScene(final Scene scene) {
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
    
    void notifyPriorityChange() {
        this.scenes.sort(SCENE_PRIORITY_COMPARATOR);
    }
    
}
