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

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.scene.SceneBuilder;
import de.omnikryptec.graphics.shader.base.parser.ShaderParser;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.resource.loadervpc.LoadingProgressCallback;
import de.omnikryptec.resource.loadervpc.ResourceProcessor;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class ComfortTest extends EngineLoader {

    public static void main(final String[] args) {
        new ComfortTest().start();
    }

    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisettings) {
        libsettings.set(LibSetting.DEBUG, true);
        windowSettings.set(WindowSetting.Name, "ComfortTest-Window");
    }

    @Override
    protected void onInitialized() {
        final SceneBuilder builder = getGameController().getGlobalScene().createBuilder();
        //builder.addGraphicsClearTest();
        //final SceneBuilder builder = new SceneBuilder();

        builder.addGraphicsClearTest();
        //builder.addGraphicsBasicImplTest();

        //getGameController().setLocalScene(builder.get());
    }

}
