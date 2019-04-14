package de.omnikryptec.libapi.exposed;

public interface Deletable {
    
    default void deleteAndUnregister() {
        deleteRaw();
        AutoDeletionManager.unregister(this);
    }
    //TODO rename
    default void register() {
        AutoDeletionManager.register(this);
    }
    
    void deleteRaw();
}
