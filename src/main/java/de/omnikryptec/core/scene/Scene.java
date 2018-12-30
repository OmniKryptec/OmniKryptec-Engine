package de.omnikryptec.core.scene;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.core.UpdateableContainer;

/**
 * A class merging sync and async {@link Updateable}s and so form a Scene.
 * 
 * @author pcfreak9000
 *
 */
public class Scene {
    
    //Rendering and Mainthread stuff
    private final UpdateableContainer updtContainerSync;
    //Might happen on another Thread
    private final UpdateableContainer updtContainerAsync;
    
    public Scene() {
        this.updtContainerSync = new UpdateableContainer();
        this.updtContainerAsync = new UpdateableContainer();
    }
    
    /**
     * A simple method to add a synchronized {@link Updateable}. A more
     * sophisticated approach can be achieved with a {@link SceneBuilder}
     * 
     * @param updt the {@link Updateable}
     * 
     * @see #createBuilder()
     */
    public void addUpdateable(final Updateable updt) {
        this.addUpdateable(true, updt);
    }
    
    /**
     * A simple method to add an {@link Updateable} to this scene. A more
     * sophisticated approach can be achieved with a {@link SceneBuilder}
     * 
     * @param sync if the {@link Updateable} should be synchronized or not
     * @param updt the {@link Updateable}
     * @see #createBuilder()
     */
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
    
    /**
     * Creates a new {@link SceneBuilder} for this {@link Scene}.
     * 
     * @return the scene builder
     */
    public SceneBuilder createBuilder() {
        //TODO cache or good enough?
        return new SceneBuilder(this);
    }
    
}
