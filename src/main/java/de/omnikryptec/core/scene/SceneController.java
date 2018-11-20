package de.omnikryptec.core.scene;

import de.omnikryptec.core.UpdateableContainer;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class SceneController {

    public enum ControllerSettings implements Defaultable{
        UPDATES_SYNC_PER_S(0), UPDATES_ASYNC_PER_S(0);

        private final Object object;
        
        private ControllerSettings(Object object) {
            this.object = object;
        }
        
        @Override
        public <T> T getDefault() {
            return (T) object;
        }
    }
    
    private final Settings<ControllerSettings> controllerSettings;
    
    //Rendering and Mainthread stuff
    private UpdateableContainer updtContainerSync;
    //Might happen on another Thread
    private UpdateableContainer updtContainerAsync;

    //    private EventBus eventbusSync;
    //    private EventBus eventbusAsync;

    public SceneController() {
        this.controllerSettings = new Settings<>();
        this.updtContainerSync = new UpdateableContainer();
        this.updtContainerAsync = new UpdateableContainer();
        //        this.eventbusSync = new EventBus();
        //        this.eventbusAsync = new EventBus();
        //        this.updtContainerSync.addUpdateables(eventbusSync);
        //        this.updtContainerAsync.addUpdateables(eventbusAsync);
    }

    public UpdateableContainer getUpdateableContainerSync() {
        return updtContainerSync;
    }

    public UpdateableContainer getUpdateableContainerAsync() {
        return updtContainerAsync;
    }
    
    public Settings<ControllerSettings> getSettings(){
        return controllerSettings;
    }
}
