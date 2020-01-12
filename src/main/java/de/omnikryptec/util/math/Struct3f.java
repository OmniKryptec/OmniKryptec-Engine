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

package de.omnikryptec.util.math;

import java.util.Objects;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Struct3f {
    
    public final float x, y, z;
    
    public Struct3f(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Struct3f(final Vector3fc invec) {
        this(invec.x(), invec.y(), invec.z());
    }
    
    public Vector3f dynamic() {
        return new Vector3f(this.x, this.y, this.z);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Struct3f) {
            final Struct3f other = (Struct3f) obj;
            return other.x == this.x && other.y == this.y && other.z == this.z;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }
}
