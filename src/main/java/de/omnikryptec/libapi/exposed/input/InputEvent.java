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

package de.omnikryptec.libapi.exposed.input;

import org.joml.Vector2fc;

import de.omnikryptec.event.Event;

public class InputEvent extends Event {
    
    public InputEvent() {
        this.consumeable = true;
    }
    
    public static class KeyEvent extends InputEvent {
        
        public final int key;
        public final int scancode;
        public final int action;
        public final int mods;
        
        public KeyEvent(final int key, final int scancode, final int action, final int mods) {
            this.key = key;
            this.scancode = scancode;
            this.action = action;
            this.mods = mods;
        }
        
    }
    
    public static class MouseButtonEvent extends InputEvent {
        
        public final int button;
        public final int action;
        public final int mods;
        
        public MouseButtonEvent(final int button, final int action, final int mods) {
            this.button = button;
            this.action = action;
            this.mods = mods;
        }
    }
    
    public static class MousePositionEvent extends InputEvent {
        
        public final double xPos;
        public final double yPos;
        public final float xRel;
        public final float yRel;
        /**
         * If the mouse moved to a position inside the viewport. if the mouse leaves the
         * window this might not be updated correctly.
         */
        public final boolean inViewport;
        
        public MousePositionEvent(final double xPos, final double yPos, Vector2fc rel, boolean inVp) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.xRel = rel.x();
            this.yRel = rel.y();
            this.inViewport = inVp;
        }
    }
    
    public static class MouseScrollEvent extends InputEvent {
        
        public final double xChange;
        public final double yChange;
        
        public MouseScrollEvent(final double xChange, final double yChange) {
            this.xChange = xChange;
            this.yChange = yChange;
        }
    }
    
    public static class CursorInWindowEvent extends InputEvent {
        
        public final boolean entered;
        
        public CursorInWindowEvent(final boolean entered) {
            this.entered = entered;
        }
        
    }
    
}
