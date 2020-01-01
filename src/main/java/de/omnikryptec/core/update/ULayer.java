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

package de.omnikryptec.core.update;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

import java.util.ArrayList;
import java.util.Collection;

public class ULayer implements IUpdatable {
    
    private final Collection<IUpdatable> updatablesActive;
    
    public ULayer() {
        this.updatablesActive = new ArrayList<>();
    }
    
    public void addUpdatable(final IUpdatable updatable) {
        Util.ensureNonNull(updatable);
        this.updatablesActive.add(updatable);
        
    }
    
    public void removeUpdatable(final IUpdatable updatable) {
        Util.ensureNonNull(updatable);
        this.updatablesActive.remove(updatable);
    }
    
    @Override
    public void update(final Time time) {
        for (final IUpdatable updatable : this.updatablesActive) {
            updatable.update(time);
        }
    }
    
}
