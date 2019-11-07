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
    
    private List<Scene> scenes;
    
    private InputManager input;
    
    private RendererContext rendererContext;
    
    private EventBus eventBus;
    
    private WindowUpdater windowUpdater;
    
    private boolean enableRenderContext = true;
    
    public Game(KeySettings keySettings) {
        this.scenes = new ArrayList<>();
        this.input = new InputManager(keySettings);
        this.rendererContext = new RendererContext();
        this.windowUpdater = new WindowUpdater(rendererContext.getRenderAPI().getWindow());
        this.eventBus = new EventBus(false);
    }
    
    public Scene createNewScene() {
        Scene newScene = new Scene(rendererContext.createLocal(), this, 0);
        scenes.add(newScene);
        notifyPriorityChange();
        return newScene;
    }
    
    @Deprecated //TODO better use one GuiManager for one instance of Game?
    public GuiManager createNewGuiManager() {
        return new GuiManager(rendererContext.createLocal());
    }
    
    //*******************************************
    
    public void prepareGame(Time time) {
        input.update(time);
    }
    
    public void updateGame(Time time) {
        for (int i = scenes.size() - 1; i >= 0; i--) {
            scenes.get(i).updateScene(time);
            eventBus.processQueuedEvents();
        }
    }
    
    public void renderGame(Time time) {
        if (enableRenderContext) {
            rendererContext.renderComplete(time);
        }
    }
    
    //*******************************************
    
    public WindowUpdater getWindowUpdater() {
        return windowUpdater;
    }

    public void setDefaultRenderingEnabled(boolean b) {
        this.enableRenderContext = b;
    }
    
    public boolean getDefaultRenderingEnabled() {
        return this.enableRenderContext;
    }
    
    public void removeScene(Scene scene) {
        this.scenes.remove(scene);
    }
    
    public InputManager getInput() {
        return input;
    }
    
    public void setEventBus(EventBus bus) {
        this.eventBus = bus;
    }
    
    public EventBus getEventBus() {
        return eventBus;
    }
    
    void notifyPriorityChange() {
        scenes.sort(SCENE_PRIORITY_COMPARATOR);
    }
    
}
