package de.omnikryptec.libapi.exposed.render;

import java.util.ArrayDeque;
import java.util.Deque;

public class FrameBufferStack {
    private final Deque<FrameBuffer> frameBufferStack = new ArrayDeque<>();
    private FrameBuffer tmp;
    
    void bind(FrameBuffer fb) {
        FrameBuffer peek = frameBufferStack.peek();
        if (peek != fb) {
            if (peek != null) {
                peek.unbindRaw();
            }
            frameBufferStack.push(fb);
            fb.bindRaw();
        }
    }
    
    void unbind(FrameBuffer fb) {
        if (frameBufferStack.size() > 1) {
            if (frameBufferStack.peek() == fb) {
                frameBufferStack.pop().unbindRaw();
                frameBufferStack.peek().bindRaw();
            }
        }
    }
    
    void bindTmp(FrameBuffer fb) {
        if (tmp != null) {
            throw new IllegalStateException("tmp already in use");
        }
        if (fb != getCurrent()) {
            tmp = fb;
            bind(tmp);
        }
    }
    
    void unbindTmp() {
        if (tmp != null) {
            unbind(tmp);
            tmp = null;
        }
    }
    
    public FrameBuffer getCurrent() {
        return frameBufferStack.peek();
    }
}
