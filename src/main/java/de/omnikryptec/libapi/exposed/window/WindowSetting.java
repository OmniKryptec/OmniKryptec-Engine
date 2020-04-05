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

package de.omnikryptec.libapi.exposed.window;

import de.omnikryptec.libapi.exposed.input.CursorType;
import de.omnikryptec.util.settings.Defaultable;

public enum WindowSetting implements Defaultable {
    Width(800), Height(600), Fullscreen(false), Name("Display"), Resizeable(true), LockAspectRatio(false),
    /**
     * @see WindowInterfaceWIP#setVSync(boolean)
     * @see de.omnikryptec.core.scene.UpdateController#setSyncUpdatesPerSecond(int)
     * @see de.omnikryptec.core.scene.UpdateController#setAsyncUpdatesPerSecond(int)
     */
    VSync(true), CursorState(CursorType.NORMAL), Decorated(true);

    private final Object def;

    WindowSetting(final Object def) {
        this.def = def;
    }

    @Override
    public <T> T getDefault() {
        return (T) this.def;
    }
}
