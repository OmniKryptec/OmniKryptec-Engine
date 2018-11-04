/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.core.test_p_1;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.updater.Time;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UpdateableContainer implements Updateable {
    
    private final List<Updateable> updateables = new CopyOnWriteArrayList<>();
    
    public boolean addUpdateable(Updateable updateable) {
        Util.ensureNonNull(updateable);
        return updateables.add(updateable);
    }
    
    public boolean addUpdateable(Updateable... updateables) {
        Util.ensureNonNull(updateables);
        return this.updateables.addAll(Arrays.asList(updateables));
    }
    
    public boolean removeUpdateable(Updateable updateable) {
        Util.ensureNonNull(updateable);
        return updateables.remove(updateable);
    }
    
    public boolean removeUpdateables(Updateable... updateables) {
        Util.ensureNonNull(updateables);
        return this.updateables.removeAll(Arrays.asList(updateables));
    }
    
    public boolean clearUpdateables() {
        updateables.clear();
        return updateables.isEmpty();
    }
    
    @Override
    public boolean preUpdate(Time time) {
        boolean done = true;
        for (Updateable updateable : updateables) {
            if (!updateable.preUpdate(time)) {
                done = false;
            }
        }
        return done;
    }
    
    @Override
    public boolean update(Time time) {
        boolean done = true;
        for (Updateable updateable : updateables) {
            if (!updateable.update(time)) {
                done = false;
            }
        }
        return done;
    }
    
    @Override
    public boolean postUpdate(Time time) {
        boolean done = true;
        for (Updateable updateable : updateables) {
            if (!updateable.postUpdate(time)) {
                done = false;
            }
        }
        return done;
    }
    
}
