package de.omnikryptec.libapi.exposed.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;

public class VertexBufferLayout {
    
    public static class VertexBufferElement {
        private Type type;
        private int count;
        private boolean normalized;
        
        public VertexBufferElement(Type type, int count, boolean normalized) {
            this.type = type;
            this.count = count;
            this.normalized = normalized;
        }
        
        public Type getType() {
            return type;
        }
        
        public int getCount() {
            return count;
        }
        
        public boolean isNormalized() {
            return normalized;
        }
        
    }
    
    private List<VertexBufferElement> elements;
    
    public VertexBufferLayout() {
        this.elements = new ArrayList<>();
    }
    
    public void push(Type type, int count, boolean normalized) {
        elements.add(new VertexBufferElement(type, count, normalized));
    }

    public List<VertexBufferElement> getElements() {
        return Collections.unmodifiableList(elements);
    }
}
