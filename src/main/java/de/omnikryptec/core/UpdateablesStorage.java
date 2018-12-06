package de.omnikryptec.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class UpdateablesStorage implements Updateable {

    public static enum ExecuteMode {
        Embracing, OneByOne
    }

    private Multimap<ExecuteMode, Updateable> updateables;

    public UpdateablesStorage() {
        this.updateables = MultimapBuilder.enumKeys(ExecuteMode.class).arrayListValues().build();
    }

    public void addUpdateable(Updateable updt) {
        Util.ensureNonNull(updt);
        addUpdateable(updt.defaultExecuteMode(), updt);
    }

    public void addUpdateable(ExecuteMode exmode, Updateable updt) {
        Util.ensureNonNull(updt);
        if (updt == this) {
            throw new IllegalArgumentException("argument == this");
        }
        updateables.put(exmode, updt);
    }

    @Override
    public void preUpdate(Time time) {
        for (Updateable updt : updateables.get(ExecuteMode.Embracing)) {
            updt.preUpdate(time);
        }
    }

    @Override
    public void update(Time time) {
        for (Updateable updt : updateables.get(ExecuteMode.OneByOne)) {
            updt.preUpdate(time);
            updt.update(time);
            updt.postUpdate(time);
        }
        for (Updateable updt : updateables.get(ExecuteMode.Embracing)) {
            updt.update(time);
        }
    }

    @Override
    public void postUpdate(Time time) {
        for (Updateable updt : updateables.get(ExecuteMode.Embracing)) {
            updt.postUpdate(time);
        }
    }
}
