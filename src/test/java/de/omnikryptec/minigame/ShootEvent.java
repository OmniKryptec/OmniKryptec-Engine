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

import de.omnikryptec.event.Event;
import org.joml.Vector2f;

public class ShootEvent extends Event {
    
    public static enum Projectile {
        Normal, Bomb;
    }
    
    public Vector2f dir;
    public float x, y;
    public float range;
    public Projectile projectile;
    
    private ShootEvent(final float x, final float y, final Vector2f dir) {
        this.dir = dir;
        this.x = x;
        this.y = y;
    }
    
    public ShootEvent(final float x, final float y, final Vector2f dir, final float range, final Projectile pro) {
        this(x, y, dir);
        this.range = range;
        this.projectile = pro;
    }
    
}
