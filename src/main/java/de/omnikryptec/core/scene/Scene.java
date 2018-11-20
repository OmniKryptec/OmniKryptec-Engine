package de.omnikryptec.core.scene;

import de.omnikryptec.core.UpdateableContainer;

public class Scene {

    //Rendering and Mainthread stuff
    private UpdateableContainer updtContainerSync;
    //Might happen on another Thread
    private UpdateableContainer updtContainerAsync;
    
    public Scene() {
        this.updtContainerSync = new UpdateableContainer();
        this.updtContainerAsync = new UpdateableContainer();
    }
    
    public UpdateableContainer getUpdateableContainerSync() {
        return updtContainerSync;
    }
    
    public UpdateableContainer getUpdateableContainerAsync() {
        return updtContainerAsync;
    }
}
