package de.omnikryptec.core.scene;

import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class GameController {

    public enum ControllerSetting implements Defaultable {
        UPDATES_SYNC_PER_S(144), UPDATES_ASYNC_PER_S(144);

        private final Object object;

        private ControllerSetting(final Object object) {
            this.object = object;
        }

        @Override
        public <T> T getDefault() {
            return (T) this.object;
        }
    }

    private final Settings<ControllerSetting> controllerSettings;
    private final Scene globalScene;
    private Scene localScene;

    public GameController() {
        this.controllerSettings = new Settings<>();
        this.globalScene = new Scene();
    }

    public Settings<ControllerSetting> getSettings() {
        return controllerSettings;
    }

    public Scene getGlobalScene() {
        return globalScene;
    }

    public Scene getLocalScene() {
        return localScene;
    }

    public boolean hasScene() {
        return localScene != null;
    }

    public void setLocalScene(Scene scene) {
        this.localScene = scene;
    }

    public void updateSync(Time time) {
        getGlobalScene().getUpdateableContainerSync().preUpdate(time);
        if (hasScene()) {
            getLocalScene().getUpdateableContainerSync().preUpdate(time);
            getLocalScene().getUpdateableContainerSync().update(time);
            getLocalScene().getUpdateableContainerSync().postUpdate(time);
        }
        getGlobalScene().getUpdateableContainerSync().update(time);
        getGlobalScene().getUpdateableContainerSync().postUpdate(time);
    }
    
    public void updateAsync(Time time) {
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
