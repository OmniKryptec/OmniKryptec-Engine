package de.omnikryptec.ecs;

import javax.annotation.Nonnull;

public interface EntityListener {

    void entityAdded(@Nonnull Entity entity);

    void entityRemoved(@Nonnull Entity entity);

}
