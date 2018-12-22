package de.omnikryptec.core.scene;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.core.UpdateableContainer;

public class Scene {

    //Rendering and Mainthread stuff
    private final UpdateableContainer updtContainerSync;
    //Might happen on another Thread
    private final UpdateableContainer updtContainerAsync;

    public Scene() {
        this.updtContainerSync = new UpdateableContainer();
        this.updtContainerAsync = new UpdateableContainer();
    }

    public void addUpdateable(final Updateable updt) {
        this.addUpdateable(true, updt);
    }

    public void addUpdateable(final boolean sync, final Updateable updt) {
        if (sync) {
            getUpdateableContainerSync().addUpdateable(updt);
        } else {
            getUpdateableContainerAsync().addUpdateable(updt);
        }
    }

    public UpdateableContainer getUpdateableContainerSync() {
        return this.updtContainerSync;
    }

    public UpdateableContainer getUpdateableContainerAsync() {
        return this.updtContainerAsync;
    }

    public SceneBuilder createBuilder() {
        //TODO cache or good enough?
        return new SceneBuilder(this);
    }

}
