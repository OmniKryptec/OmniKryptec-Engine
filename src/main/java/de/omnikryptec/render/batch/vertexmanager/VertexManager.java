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

package de.omnikryptec.render.batch.vertexmanager;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;

public interface VertexManager {
    
    void init(ModuleBatchingManager mgr);
    
    default void addData(final float... fs) {
        this.addData(fs, 0, fs.length);
    }
    
    void addData(float[] floats, int offset, int length);
    
    void prepareNext(Texture baseTexture, int requiredFloats);
    
    void forceFlush();
    
    void begin();
    
}
