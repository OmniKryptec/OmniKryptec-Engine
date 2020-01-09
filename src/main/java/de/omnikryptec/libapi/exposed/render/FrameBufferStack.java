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
