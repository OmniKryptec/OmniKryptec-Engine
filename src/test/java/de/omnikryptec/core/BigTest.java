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

package de.omnikryptec.core;

import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.core.update.ULayer;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.render.objects.AdvancedSprite;
import de.omnikryptec.render.objects.AdvancedSprite.Reflection2DType;
import de.omnikryptec.render.renderer.AdvancedRenderer2D;
import de.omnikryptec.resource.loadervpc.LoadingProgressCallback;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class BigTest extends Omnikryptec {

    public static void main(final String[] args) {
        //Logger.getDefaultAdvancedLeveledLogger().setMinimumLogLevel(LogLevel.FINEST);
        //Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendLogLevel().appendText(": ").appendObject().appendNewLine().appendThread().appendSource().appendNewLine().finishWithoutException();
        //AdvancedFile.DEBUG = true;
        //AdvancedFile.DEBUG_TO_STRING = true;
        new BigTest().start();
    }

    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisettings,
            final KeySettings keys) {
        libsettings.set(LibSetting.DEBUG, true);
        libsettings.set(LibSetting.LOGGING_MIN, LogType.Debug);
        windowSettings.set(WindowSetting.Name, "BigTest-Window");
        windowSettings.set(WindowSetting.LockAspectRatio, true);
        windowSettings.set(WindowSetting.VSync, false);

        //windowSettings.set(WindowSetting.Fullscreen, true);
        //windowSettings.set(WindowSetting.Width, 10000);
        //windowSettings.set(WindowSetting.Height, 2000);
    }

    @Override
    protected void onInitialized() {
        final Scene actual = getGame().createAndAddScene();
        final ULayer scene = new ULayer();
        actual.setGameLogic(scene);
        getResourceManager().stage("intern:/de/omnikryptec/resources/");
        getResourceManager().addCallback(LoadingProgressCallback.DEBUG_CALLBACK);
        getResourceManager().processStaged(false, false);
        //scene.addUpdatable(UpdateableFactory.createScreenClearTest());
        //scene.addUpdatable(UpdateableFactory.createRenderTest(getTextures()));
        AdvancedRenderer2D renderer = actual.getViewManager().createAndAddAdvancedRenderer2D();
        final AdvancedSprite s = new AdvancedSprite();
        //s.setColor(new Color(1, 0, 0));
        s.setTilingFactor(5);
        s.setReflectionType(Reflection2DType.Cast);
        s.getTransform().localspaceWrite().rotate(Mathf.PI / 4);
        s.getTransform().localspaceWrite().setTranslation(0.5f, 0.5f);
        scene.addUpdatable(new IUpdatable() {
            private float f;
            private int fac = 1;

            @Override
            public void update(final Time time) {
                s.getTransform().localspaceWrite().translate(0.5f * s.getWidth(), 0.5f * s.getHeight());
                s.getTransform().localspaceWrite().rotate(time.deltaf * this.fac);
                this.f += time.deltaf * this.fac;
                if (this.f > Mathf.PI / 4 || this.f < -Mathf.PI / 4) {
                    this.fac *= -1;
                }
                s.getTransform().localspaceWrite().translate(-0.5f * s.getWidth(), -0.5f * s.getHeight());
            }
        });
        //s.setPosition(new Vector2f(0.5f));
        s.setWidth(0.25f);
        s.setHeight(0.25f);
        s.setLayer(1);
        s.setTexture(getTextures().get("jd.png"));
        s.setOffset(-0.2f);
        final AdvancedSprite back = new AdvancedSprite();
        back.setTexture(getTextures().get("jn.png"));
        back.setHeight(0.4f);
        back.setReflectionType(Reflection2DType.Receive);
        back.reflectiveness().set(1, 1, 1);
        final AdvancedSprite back2 = new AdvancedSprite();
        back2.setHeight(0.6f);
        back2.getTransform().localspaceWrite().translate(0, 0.4f);
        back2.setReflectionType(Reflection2DType.Disable);
        scene.addUpdatable(time -> back2.setColor(Color.ofTemperature(Mathf.pingpong(time.currentf, 20000))));
        renderer.add(back2);
        renderer.add(back);
        renderer.add(s);
    }

    @Override
    protected void onShutdown() {
        System.out.println(Profiler.currentInfo());
    }
}
