package de.omnikryptec.libapi.exposed.input;

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

        public MousePositionEvent(final double xPos, final double yPos) {
            this.xPos = xPos;
            this.yPos = yPos;
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
