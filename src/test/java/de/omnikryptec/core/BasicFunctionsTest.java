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

package de.omnikryptec.core;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.IWindow;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Settings;

/**
 * a rather advanced way of using this engine.
 *
 * @author pcfreak9000
 *
 */
public class BasicFunctionsTest {
    
    public static void main(final String[] args) {
        LibAPIManager.init(new Settings<>());
        LibAPIManager.instance().initGlfw();
        LibAPIManager.instance().getGLFW().setRenderer(RenderAPI.OpenGL, new Settings<>(), new Settings<>());
        final IWindow window = LibAPIManager.instance().getGLFW().getRenderAPI().getWindow();
        window.setVisible(true);
        final WindowUpdater updater = new WindowUpdater(window);
        final Color clearColor = new Color();
        while (!window.isCloseRequested()) {
            updater.update();
            if (updater.getOperationCount() % 40 == 0) {
                clearColor.randomizeRGB();
            }
            LibAPIManager.instance().getGLFW().getRenderAPI().getSurface().clearColor(clearColor);
        }
        LibAPIManager.shutdown();
    }
    
}
