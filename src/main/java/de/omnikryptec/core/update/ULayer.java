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

package de.omnikryptec.core.update;

import java.util.ArrayList;
import java.util.Collection;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public class ULayer implements IUpdatable, ILayer {
    
    private EventBus eventBus;
    
    private boolean isInitialized;
    
    private Collection<IUpdatable> updatablesActive;
    private Collection<IUpdatable> updatablesPassive;
    
    public ULayer() {
        this.eventBus = new EventBus();
        this.updatablesActive = new ArrayList<>();
        this.updatablesPassive = new ArrayList<>();
    }
    
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
    
    public void addUpdatable(IUpdatable updatable) {
        Util.ensureNonNull(updatable);
        if (updatable.passive()) {
            updatablesPassive.add(updatable);
        } else {
            updatablesActive.add(updatable);
        }
        if (isInitialized) {
            updatable.init(this);
        }
    }
    
    public void removeUpdatable(IUpdatable updatable) {
        Util.ensureNonNull(updatable);
        if (updatable.passive()) {
            updatablesPassive.remove(updatable);
        } else {
            updatablesActive.remove(updatable);
        }
        if (isInitialized) {
            updatable.deinit(this);
        }
    }
    
    @Override
    public boolean passive() {
        return false;
    }
    
    @Override
    public void update(Time time) {
        for (IUpdatable updatable : updatablesActive) {
            updatable.update(time);
        }
    }
    
    @Override
    public void init(ILayer layer) {
        if (layer != null && layer.getEventBus() != eventBus) {
            layer.getEventBus().register(eventBus);
        }
        for (IUpdatable updatable : updatablesActive) {
            updatable.init(this);
        }
        for (IUpdatable updatable : updatablesPassive) {
            updatable.init(this);
        }
        isInitialized = true;
    }
    
    @Override
    public void deinit(ILayer layer) {
        if (layer != null && layer.getEventBus() != eventBus) {
            layer.getEventBus().unregister(eventBus);
        }
        for (IUpdatable updatable : updatablesActive) {
            updatable.deinit(this);
        }
        for (IUpdatable updatable : updatablesPassive) {
            updatable.deinit(this);
        }
        isInitialized = false;
    }
    
}
