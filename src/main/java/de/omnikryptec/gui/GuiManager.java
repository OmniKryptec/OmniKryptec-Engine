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

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.input.InputEvent;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.renderer.ViewManager;

public class GuiManager {

    private final ViewManager viewMgr;
    private GuiRenderer renderer;

    private GuiComponent componentRoot;

    public GuiManager(final ViewManager vm) {
        this.viewMgr = vm;
        LibAPIManager.ENGINE_EVENTBUS.register(this);
        setRenderer(new GuiRenderer());
    }

    public void setRenderer(final GuiRenderer renderer) {
        if (this.renderer != null) {
            this.viewMgr.removeRenderer(renderer);
            this.renderer.setGui(null);
        }
        this.renderer = renderer;
        if (this.renderer != null) {
            this.viewMgr.addRenderer(renderer);
            if (this.componentRoot != null) {
                this.renderer.setGui(this.componentRoot);
            }
        }
    }

    public void setGui(final GuiComponent componentRoot) {
        this.componentRoot = componentRoot;
        if (this.renderer != null) {
            this.renderer.setGui(componentRoot);
        }
        recalculateConstraints();
    }

    @Deprecated
    public void setGuiProjection(IProjection proj) {
        this.viewMgr.getMainView().setProjection(proj);
    }

    private void recalculateConstraints() {
        if (this.componentRoot != null) {
            this.componentRoot.setConstraints(new GuiConstraints(0, 0, 1, 1));
        }
    }

    @EventSubscription(priority = 2000, receiveConsumed = false)
    public void event(final InputEvent event) {
        if (this.componentRoot != null) {
            this.componentRoot.getEventBus().post(event);
        }
    }
}
