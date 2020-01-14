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
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.AdvancedBatch2D;
import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.render.renderer.Renderer;
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
    public void init(final LocalRendererContext context, final FrameBuffer target) {
    }
    
    @Override
    public void render(final Time time, final IProjection projection, final LocalRendererContext context) {
        if (this.componentRoot != null) {
            batch.getShaderSlot().setProjection(projection);
            this.batch.begin();
            this.componentRoot.render(this.batch);
            this.batch.end();
        }
    }
    
    @Override
    public void deinit(final LocalRendererContext context) {
    }
    
}
