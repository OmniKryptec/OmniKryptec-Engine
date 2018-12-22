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

        public VertexBufferElement(final Type type, final int count, final boolean normalized) {
            this.type = type;
            this.count = count;
            this.normalize = normalized;
        }

        public Type getType() {
            return this.type;
        }

        public int getCount() {
            return this.count;
        }

        public boolean normalize() {
            return this.normalize;
        }

    }

    private final List<VertexBufferElement> elements;

    public VertexBufferLayout() {
        this.elements = new ArrayList<>();
    }

    public void push(final Type type, final int count, final boolean normalized) {
        this.elements.add(new VertexBufferElement(type, count, normalized));
    }

    public List<VertexBufferElement> getElements() {
        return Collections.unmodifiableList(this.elements);
    }
}
