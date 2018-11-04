/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.libapi.glfw.input;

import org.lwjgl.glfw.GLFW;

public enum CursorType {
    
    NORMAL(GLFW.GLFW_CURSOR_NORMAL),
    HIDDEN(GLFW.GLFW_CURSOR_HIDDEN),
    DISABLED(GLFW.GLFW_CURSOR_DISABLED);
    
    private final int state;
    
    CursorType(int state) {
        this.state = state;
    }
    
    public static CursorType ofState(int state) {
        for (CursorType cursorType : values()) {
            if (cursorType.state == state) {
                return cursorType;
            }
        }
        return DISABLED;
    }
    
    public int getState() {
        return state;
    }
    
}