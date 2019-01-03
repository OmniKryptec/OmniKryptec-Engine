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

package de.omnikryptec.resource;

import java.util.EnumMap;
import java.util.Map;

public class MeshData {
    
    public static enum PrimitiveType {
        POINT(1), LINE(2), Triangle(3), Quad(4);
        
        private PrimitiveType(int vc) {
            this.vertexCount = vc;
        }
        
        public final int vertexCount;
    }
    
    public static enum VertexAttribute {
        Position, Normal, Tangent, Bitangent, TextureCoord, Index;
    }
    
    private PrimitiveType primitiveType;
    
    private Map<VertexAttribute, Object> vertexData = new EnumMap<>(VertexAttribute.class);
    
    public MeshData(Object... objects) {
        if (objects.length % 2 != 0) {
            throw new IllegalArgumentException();
        }
        VertexAttribute current = null;
        for (Object o : objects) {
            if (o instanceof VertexAttribute) {
                current = (VertexAttribute) o;
            } else {
                vertexData.put(current, o);
            }
        }
    }
    
    public boolean hasVertexAttribute(VertexAttribute attribute) {
        return vertexData.containsKey(attribute);
    }
    
    public <T> T getAttribute(VertexAttribute attribute) {
        return (T) vertexData.get(attribute);
    }
    
    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }
}
