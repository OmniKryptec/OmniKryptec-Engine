package de.omnikryptec.core;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class UpdateContainer implements Updateable {

    private final List<Updateable> updateables;

    public UpdateContainer() {
        updateables = new ArrayList<>();
    }

    public UpdateContainer addUpdateable(Updateable updateable) {
        Util.ensureNonNull(updateable);
        if (updateable == this) {
            throw new IllegalArgumentException("Can't add this");
        }
        updateables.add(updateable);
        return this;
    }

    @Override
    public void preUpdate(Time time) {
        for (Updateable updateable : updateables) {
            updateable.preUpdate(time);
        }
    }

    @Override
    public void update(Time time) {
        for (Updateable updateable : updateables) {
            updateable.update(time);
        }
    }

    @Override
    public void postUpdate(Time time) {
        for (Updateable updateable : updateables) {
            updateable.postUpdate(time);
        }
    }

}