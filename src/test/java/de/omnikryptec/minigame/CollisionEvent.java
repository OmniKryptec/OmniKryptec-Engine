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

package de.omnikryptec.minigame;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.event.Event;

public class CollisionEvent extends Event {
    public final Entity e1;
    public final Entity e2;
    
    public CollisionEvent(final Entity e1, final Entity e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    
    public boolean hasFlags(final int flags) {
        return this.e1.flags == flags || this.e2.flags == flags;
    }
    
    public Entity getEntity(final int flags) {
        return this.e1.flags == flags ? this.e1 : (this.e2.flags == flags ? this.e2 : null);
    }
}
