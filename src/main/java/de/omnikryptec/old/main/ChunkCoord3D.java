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

package de.omnikryptec.old.main;

public class ChunkCoord3D {

    public final long x, y, z;

    public ChunkCoord3D(long x, long y, long z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj instanceof ChunkCoord3D) {
	    ChunkCoord3D t = (ChunkCoord3D) obj;
	    if (t.x == x && t.y == y && t.z == z) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public int hashCode() {
	return (int) ((((((0) ^ x) * 397) ^ y) * 397) ^ z);
    }

}
