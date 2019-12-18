package de.omnikryptec.libapi.exposed.render;

import java.util.ArrayDeque;
import java.util.Deque;

public class FrameBufferStack {
    private final Deque<FrameBuffer> frameBufferStack = new ArrayDeque<>();
    private FrameBuffer tmp;
    
    void bind(final FrameBuffer fb) {
        final FrameBuffer peek = this.frameBufferStack.peek();
        if (peek != fb) {
            if (peek != null) {
                peek.unbindRaw();
            }
            this.frameBufferStack.push(fb);
            fb.bindRaw();
        }
    }
    
    void unbind(final FrameBuffer fb) {
        if (this.frameBufferStack.size() > 1) {
            if (this.frameBufferStack.peek() == fb) {
                this.frameBufferStack.pop().unbindRaw();
                this.frameBufferStack.peek().bindRaw();
            }
        }
    }
    
    void bindTmp(final FrameBuffer fb) {
        if (this.tmp != null) {
            throw new IllegalStateException("tmp already in use");
        }
        if (fb != getCurrent()) {
            this.tmp = fb;
            bind(this.tmp);
        }
    }
    
    void unbindTmp() {
        if (this.tmp != null) {
            unbind(this.tmp);
            this.tmp = null;
        }
    }
    
    public FrameBuffer getCurrent() {
        return this.frameBufferStack.peek();
    }
}
