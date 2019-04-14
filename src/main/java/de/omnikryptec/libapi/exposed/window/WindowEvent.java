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

package de.omnikryptec.libapi.exposed.window;

import de.omnikryptec.event.Event;

public class WindowEvent extends Event {
    
    public final IWindow window;
    
    protected WindowEvent(final IWindow window) {
        this.window = window;
    }
    
    public static class WindowFocused extends WindowEvent {
        public final boolean focused;
        
        public WindowFocused(final IWindow window, final boolean focused) {
            super(window);
            this.focused = focused;
        }
    }
    
    public static class WindowResized extends WindowEvent {
        public final int width;
        public final int height;
        
        public WindowResized(final IWindow window, final int width, final int height) {
            super(window);
            this.width = width;
            this.height = height;
        }
    }
    
    public static class ScreenBufferResized extends WindowEvent {
        public final int width;
        public final int height;
        public final SurfaceBuffer surface;
        //TODO is this really a WindowEvent?
        public ScreenBufferResized(final IWindow window, final int width, final int height, SurfaceBuffer surface) {
            super(window);
            this.width = width;
            this.height = height;
            this.surface = surface;
        }
    }
    
    public static class WindowIconified extends WindowEvent {
        public final boolean iconified;
        
        public WindowIconified(final IWindow window, final boolean iconified) {
            super(window);
            this.iconified = iconified;
        }
    }
    
    public static class WindowMaximized extends WindowEvent {
        public final boolean maximized;
        
        public WindowMaximized(final IWindow window, final boolean maximized) {
            super(window);
            this.maximized = maximized;
        }
    }
    
}
