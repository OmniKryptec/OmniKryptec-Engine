package de.omnikryptec.core.scene;

import de.omnikryptec.util.updater.Time;

public class GameController {
    
    private final Scene globalScene;
    private Scene localScene;
    
    public GameController() {
        this.globalScene = new Scene();
    }
    
    public Scene getGlobalScene() {
        return this.globalScene;
    }
    
    public Scene getLocalScene() {
        return this.localScene;
    }
    
    public boolean hasScene() {
        return this.localScene != null;
    }
    
    public void setLocalScene(final Scene scene) {
        this.localScene = scene;
    }
    
    public void updateSync(final Time time) {
        getGlobalScene().getUpdateableContainerSync().preUpdate(time);
        if (hasScene()) {
            getLocalScene().getUpdateableContainerSync().preUpdate(time);
            getLocalScene().getUpdateableContainerSync().update(time);
            getLocalScene().getUpdateableContainerSync().postUpdate(time);
        }
        getGlobalScene().getUpdateableContainerSync().update(time);
        getGlobalScene().getUpdateableContainerSync().postUpdate(time);
    }
    
    public void updateAsync(final Time time) {
        getGlobalScene().getUpdateableContainerAsync().preUpdate(time);
        if (hasScene()) {
            getLocalScene().getUpdateableContainerAsync().preUpdate(time);
            getLocalScene().getUpdateableContainerAsync().update(time);
            getLocalScene().getUpdateableContainerAsync().postUpdate(time);
        }
        getGlobalScene().getUpdateableContainerAsync().update(time);
        getGlobalScene().getUpdateableContainerAsync().postUpdate(time);
    }
}
