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

import java.util.ArrayList;
import java.util.List;

import com.google.errorprone.annotations.ForOverride;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.util.Util;

public class GuiComponent {

    //Until better constraints are set so layouts etc work when creating the GUI, maybe only run layout stuff when needed like before rendering?
    private static final GuiConstraints DEFAULT_CONSTRAINTS = new GuiConstraints(0, 0, 1, 1);

    private GuiLayout layout;

    private GuiConstraints constraints;

    private boolean visible = true;

    private final List<GuiComponent> children;
    private final EventBus events;

    public GuiComponent() {
        this.layout = null;
        this.constraints = DEFAULT_CONSTRAINTS;
        this.children = new ArrayList<>();
        this.events = new EventBus(2000);
        this.events.setReceiveConsumed(false);
        this.events.register(this);
    }

    public void addComponent(final GuiComponent comp) {
        this.children.add(comp);
        this.events.register(comp.events);
        revalidateLayout();
    }

    public void removeComponent(final GuiComponent comp) {
        this.children.remove(comp);
        this.events.unregister(comp.events);
        revalidateLayout();
    }

    public void setLayout(final GuiLayout layout) {
        this.layout = layout;
        revalidateLayout();
    }

    public void revalidateLayout() {
        if (this.layout != null) {
            this.layout.doLayout(this, this.children);
        } else {
            for (final GuiComponent gc : this.children) {
                gc.setConstraints(this.constraints);
            }
        }
    }

    public void setConstraints(final GuiConstraints constraints) {
        this.constraints = Util.ensureNonNull(constraints);
        revalidateLayout();
        calculateActualPosition(this.constraints);
    }

    public GuiConstraints getConstraints() {
        return this.constraints;
    }

    public void render(final BorderedBatch2D batch, float aspect) {
        if (this.visible) {
            renderComponent(batch, aspect);
            for (final GuiComponent gc : this.children) {
                gc.render(batch, aspect);
            }
        }
    }

    EventBus getEventBus() {
        return this.events;
    }

    public void setVisibility(boolean b) {
        this.visible = b;
        this.events.setAcceptEvents(b);
    }

    @ForOverride
    protected void renderComponent(final BorderedBatch2D batch, float aspect) {

    }

    @ForOverride
    protected void calculateActualPosition(final GuiConstraints constraints) {
        //use the current constraints and some other stuff to calculate the components actual pos, width and height here
    }

}
