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

package de.omnikryptec.libapi.exposed.input;

import org.lwjgl.glfw.GLFW;

@Deprecated
public enum InputState {
    
    NOTHING(-1), RELEASED(GLFW.GLFW_RELEASE), PRESSED(GLFW.GLFW_PRESS), REPEATED(GLFW.GLFW_REPEAT);
    
    private final int state;
    
    InputState(final int state) {
        this.state = state;
    }
    
    public static final InputState ofState(final int state) {
        for (final InputState inputState : values()) {
            if (inputState.state == state) {
                return inputState;
            }
        }
        return NOTHING;
    }
    
    public final int getState() {
        return this.state;
    }
    
}
