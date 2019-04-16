package de.omnikryptec.libapi.exposed;

public interface Deletable {
    
    default void deleteAndUnregister() {
        deleteRaw();
        AutoDeletionManager.unregister(this);
    }

    default void registerThisAsAutodeletable() {
        AutoDeletionManager.register(this);
    }
    
    void deleteRaw();
}
