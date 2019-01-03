/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

import java.util.HashMap;
import java.util.Map;

public class UpdateableContainer implements Updateable {
    
    public static enum ExecuteMode {
        Embracing, OneByOne, Default
    }
    
    /**
     * This enum configures how the three methods of {@link Updateable} are actually
     * called. <br>
     * For example, the {@link Updateable#update(Time)} can be called in the
     * {@link Updateable#postUpdate(Time)} spot by using the
     * {@link ExecuteTime#OneBehind} option.
     * 
     * @author pcfreak9000
     *
     */
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
        addUpdateable(null, updt);
    }
    
    public void addUpdateable(final ExecuteMode mode, final Updateable updt) {
        addUpdateable(mode, ExecuteTime.Normal, updt);
    }
    
    public void addUpdateable(final ExecuteMode exmode, final ExecuteTime time, final Updateable updt) {
        Util.ensureNonNull(updt);
        Util.ensureNonNull(exmode);
        if (updt == this) {
            throw new IllegalArgumentException("argument == this");
        }
        this.updateables.put(exmode == ExecuteMode.Default ? updt.defaultExecuteMode() : exmode, updt);
        this.updtTimes.put(updt, Util.ensureNonNull(time));
    }
    
    public void removeUpdateable(final Updateable updt) {
        Util.ensureNonNull(updt);
        for (final ExecuteMode m : ExecuteMode.values()) {
            this.updateables.remove(m, updt);
        }
        this.updtTimes.remove(updt);
    }
    
    private void preUpdateTimed(final Time time, final Updateable updt) {
        final ExecuteTime t = this.updtTimes.get(updt);
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
    
    private void updateTimed(final Time time, final Updateable updt) {
        final ExecuteTime t = this.updtTimes.get(updt);
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
    
    private void postUpdateTimed(final Time time, final Updateable updt) {
        final ExecuteTime t = this.updtTimes.get(updt);
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
