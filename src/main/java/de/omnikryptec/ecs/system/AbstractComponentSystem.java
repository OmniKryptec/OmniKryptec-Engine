/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.ecs.system;

import java.util.BitSet;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.google.errorprone.annotations.ForOverride;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

public abstract class AbstractComponentSystem {
    
    protected List<Entity> entities;
    protected boolean enabled = true;
    private final BitSet family;
    
    public AbstractComponentSystem(final BitSet required) {
        Util.ensureNonNull(required, "BitSet must not be null (but can be empty)");
        this.family = required;
    }
    
    public BitSet getFamily() {
        return this.family;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    @OverridingMethodsMustInvokeSuper
    public void addedToIECSManager(final IECSManager iecsManager) {
        this.entities = iecsManager.getEntitesFor(this.family);
    }
    
    @OverridingMethodsMustInvokeSuper
    public void removedFromIECSManager(final IECSManager iecsManager) {
        this.entities = null;
    }
    
    public int priority() {
        return 0;
    }
    
    @ForOverride
    public void update(IECSManager iecsManager, Time time) {
    }
    
}
