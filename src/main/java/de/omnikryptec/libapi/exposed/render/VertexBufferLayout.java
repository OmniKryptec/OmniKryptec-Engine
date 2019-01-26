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

package de.omnikryptec.libapi.exposed.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;

public class VertexBufferLayout {
    
    public static class VertexBufferElement {
        private final Type type;
        private final int count;
        private final boolean normalize;
        
        /**
         *
         * @param type      the {@link Type} of the data
         * @param count     per-vertex count of the data
         * @param normalize normalize the data?
         */
        public VertexBufferElement(final Type type, final int count, final boolean normalize) {
            this.type = type;
            this.count = count;
            this.normalize = normalize;
        }
        
        /**
         * The {@link Type} of the data
         *
         * @return type of the data
         */
        public Type getType() {
            return this.type;
        }
        
        /**
         * Number of values per vertex
         *
         * @return per-vertex count
         */
        public int getCount() {
            return this.count;
        }
        
        /**
         * If the data should be normalized
         *
         * @return
         */
        public boolean normalize() {
            return this.normalize;
        }
        
    }
    
    private final List<VertexBufferElement> elements;
    
    public VertexBufferLayout() {
        this.elements = new ArrayList<>();
    }
    
    /**
     *
     * @param type      the {@link Type} of the data
     * @param count     per-vertex count of the data
     * @param normalize normalize the data?
     */
    public void push(final Type type, final int count, final boolean normalize) {
        this.elements.add(new VertexBufferElement(type, count, normalize));
    }
    
    public List<VertexBufferElement> getElements() {
        return Collections.unmodifiableList(this.elements);
    }
}
