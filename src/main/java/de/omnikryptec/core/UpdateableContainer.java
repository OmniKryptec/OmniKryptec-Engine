package de.omnikryptec.core;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class UpdateableContainer implements Updateable {
    
    public static enum ExecuteMode {
        Embracing, OneByOne
    }
    
    public static enum ExecuteTime {
        OneAhead, Normal, OneBehind
    }
    
    private final Multimap<ExecuteMode, Updateable> updateables;
    private final Map<Updateable, ExecuteTime> updtTimes;
    
    public UpdateableContainer() {
        this.updateables = MultimapBuilder.enumKeys(ExecuteMode.class).arrayListValues().build();
        this.updtTimes = new HashMap<>();
    }
    
    public void addUpdateable(final Updateable updt) {
        Util.ensureNonNull(updt);
        addUpdateable(updt.defaultExecuteMode(), updt);
    }
    
    public void addUpdateable(final ExecuteMode mode, final Updateable updt) {
        addUpdateable(mode, ExecuteTime.Normal, updt);
    }
    
    public void addUpdateable(final ExecuteMode exmode, final ExecuteTime time, final Updateable updt) {
        Util.ensureNonNull(updt);
        if (updt == this) {
            throw new IllegalArgumentException("argument == this");
        }
        this.updateables.put(exmode, updt);
        this.updtTimes.put(updt, Util.ensureNonNull(time));
    }
    
    public void removeUpdateable(final Updateable updt) {
        Util.ensureNonNull(updt);
        for (ExecuteMode m : ExecuteMode.values()) {
            updateables.remove(m, updt);
        }
        updtTimes.remove(updt);
    }
    
    private void preUpdateTimed(final Time time, Updateable updt) {
        ExecuteTime t = updtTimes.get(updt);
        switch (t) {
        case Normal:
            updt.preUpdate(time);
            break;
        case OneAhead:
            updt.update(time);
            break;
        case OneBehind:
            updt.postUpdate(time);
            break;
        default:
            throw new IllegalStateException(t + "");
        }
    }
    
    private void updateTimed(final Time time, Updateable updt) {
        ExecuteTime t = updtTimes.get(updt);
        switch (t) {
        case Normal:
            updt.update(time);
            break;
        case OneAhead:
            updt.postUpdate(time);
            break;
        case OneBehind:
            updt.preUpdate(time);
            break;
        default:
            throw new IllegalStateException(t + "");
        }
    }
    
    private void postUpdateTimed(final Time time, Updateable updt) {
        ExecuteTime t = updtTimes.get(updt);
        switch (t) {
        case Normal:
            updt.postUpdate(time);
            break;
        case OneAhead:
            updt.preUpdate(time);
            break;
        case OneBehind:
            updt.update(time);
            break;
        default:
            throw new IllegalStateException(t + "");
        }
    }
    
    @Override
    public void preUpdate(final Time time) {
        for (final Updateable updt : this.updateables.get(ExecuteMode.Embracing)) {
            preUpdateTimed(time, updt);
        }
    }
    
    @Override
    public void update(final Time time) {
        for (final Updateable updt : this.updateables.get(ExecuteMode.OneByOne)) {
            preUpdateTimed(time, updt);
            updateTimed(time, updt);
            postUpdateTimed(time, updt);
        }
        for (final Updateable updt : this.updateables.get(ExecuteMode.Embracing)) {
            updateTimed(time, updt);
        }
    }
    
    @Override
    public void postUpdate(final Time time) {
        for (final Updateable updt : this.updateables.get(ExecuteMode.Embracing)) {
            postUpdateTimed(time, updt);
        }
    }
}
