package de.omnikryptec.core;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class UpdateableContainer implements Updateable {

    private final List<Updateable> updateables;

    public UpdateableContainer() {
        updateables = new ArrayList<>();
    }

    public void addUpdateables(Updateable... updateables) {
        Util.ensureNonNull(updateables);
        for (Updateable updateable : updateables) {
            Util.ensureNonNull(updateable);
            if (updateable == this) {
                throw new IllegalArgumentException("Can't add this");
            }
            this.updateables.add(updateable);
        }
    }

    public void removeUpdateable(Updateable... updateables) {
        Util.ensureNonNull(updateables);
        for (Updateable updateable : updateables) {
            Util.ensureNonNull(updateable);
            this.updateables.remove(updateable);
        }
    }

    public void clear() {
        updateables.clear();
    }
    
    public void everyUpdate(Time time) {
        preUpdate(time);
        update(time);
        postUpdate(time);
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