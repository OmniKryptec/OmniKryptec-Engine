package de.omnikryptec.libapi.exposed.window;

import de.omnikryptec.event.Event;

public class WindowEvent extends Event {
    
    public static class WindowFocused extends WindowEvent {
        public final boolean focused;
        
        public WindowFocused(boolean focused) {
            this.focused = focused;
        }
    }
    
    public static class WindowResized extends WindowEvent {
        public final int width;
        public final int height;
        
        public WindowResized(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
    
    public static class WindowIconified extends WindowEvent {
        public final boolean iconified;
        
        public WindowIconified(boolean iconified) {
            this.iconified = iconified;
        }
    }
    
    public static class WindowMaximized extends WindowEvent {
        public final boolean maximized;
        
        public WindowMaximized(boolean maximized) {
            this.maximized = maximized;
        }
    }
    
}
