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

package de.omnikryptec.util.math;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.Objects;

public class Struct2f {
    
    public final float x, y;
    
    public Struct2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public Struct2f(final Vector2fc invec) {
        this(invec.x(), invec.y());
    }
    
    public Vector2f dynamic() {
        return new Vector2f(this.x, this.y);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Struct2f) {
            final Struct2f other = (Struct2f) obj;
            return other.x == this.x && other.y == this.y;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
}
