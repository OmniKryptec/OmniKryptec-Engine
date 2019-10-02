package de.omnikryptec.core.scene;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.render.renderer.RendererContext;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.updater.Time;

public class Game {
    
    private List<SceneNew> scenes;
    
    private InputManager input;
    
    private RendererContext rendererContext;
    
    private EventBus eventBus;
    
    private WindowUpdater windowUpdater;
    
    public Game(KeySettings keySettings) {
        this.scenes = new ArrayList<>();
        this.input = new InputManager(keySettings);
        this.rendererContext = new RendererContext();
        this.windowUpdater = new WindowUpdater(rendererContext.getRenderAPI().getWindow());
        this.eventBus = new EventBus(false);
    }
    
    public SceneNew createNewScene() {
        return new SceneNew(rendererContext.createLocal());
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
        rendererContext.renderComplete(time);
    }
    
    //*******************************************
    
    public WindowUpdater getWindowUpdater() {
        return windowUpdater;
    }
    
    public void addScene(SceneNew scene) {
        this.scenes.add(scene);
    }
    
    public void removeScene(SceneNew scene) {
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
    
}
