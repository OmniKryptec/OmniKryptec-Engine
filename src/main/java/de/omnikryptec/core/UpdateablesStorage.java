package de.omnikryptec.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class UpdateablesStorage implements Updateable {

    public static enum ExecuteMode {
        Embracing, OneByOne
    }

    private final Multimap<ExecuteMode, Updateable> updateables;

    public UpdateablesStorage() {
        this.updateables = MultimapBuilder.enumKeys(ExecuteMode.class).arrayListValues().build();
    }

    public void addUpdateable(final Updateable updt) {
        Util.ensureNonNull(updt);
        addUpdateable(updt.defaultExecuteMode(), updt);
    }

    public void addUpdateable(final ExecuteMode exmode, final Updateable updt) {
        Util.ensureNonNull(updt);
        if (updt == this) {
            throw new IllegalArgumentException("argument == this");
        }
        this.updateables.put(exmode, updt);
    }

    @Override
    public void preUpdate(final Time time) {
        for (final Updateable updt : this.updateables.get(ExecuteMode.Embracing)) {
            updt.preUpdate(time);
        }
    }

    @Override
    public void update(final Time time) {
        for (final Updateable updt : this.updateables.get(ExecuteMode.OneByOne)) {
            updt.preUpdate(time);
            updt.update(time);
            updt.postUpdate(time);
        }
        for (final Updateable updt : this.updateables.get(ExecuteMode.Embracing)) {
            updt.update(time);
        }
    }

    @Override
    public void postUpdate(final Time time) {
        for (final Updateable updt : this.updateables.get(ExecuteMode.Embracing)) {
            updt.postUpdate(time);
        }
    }
}
