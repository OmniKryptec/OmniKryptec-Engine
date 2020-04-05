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

package de.omnikryptec.gui;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.AdvancedBatch2D;
import de.omnikryptec.render.renderer.Renderer;
import de.omnikryptec.render.renderer.Renderer2D;
import de.omnikryptec.render.renderer.ViewManager;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class GuiRenderer implements Renderer {

    private GuiComponent componentRoot;
    private final AdvancedBatch2D batch;

    public GuiRenderer() {
        this.batch = new AdvancedBatch2D(1000);
    }

    protected void setGui(final GuiComponent componentRoot) {
        this.componentRoot = componentRoot;
    }

    @Override
    public void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target,
            Settings<EnvironmentKey> envSettings, Time time) {
        if (this.componentRoot != null) {
            api.applyRenderState(Renderer2D.SPRITE_STATE);
            this.batch.getShaderSlot().setProjection(projection);
            this.batch.begin();
            this.componentRoot.render(this.batch, target.getWidth() / (float) target.getHeight());
            this.batch.end();
        }
    }

}
