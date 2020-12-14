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
import java.util.Arrays;
import java.util.List;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.input.InputEvent;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.d2.compat.BorderedBatch2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.keys.KeysAndButtons;

public class GuiButton extends GuiComponentPositionable {
    
    public static enum State {
        Disabled, Clicked, Hovering, Idle;
    }
    
    private boolean enabled = true;//TODO pcfreak9000 decide about GUI enabling
    
    private List<ActionListener> actionlisteners;
    private Color[] colors = new Color[State.values().length];
    private Texture[] textures = new Texture[State.values().length];
    private boolean hovering;
    private boolean clicked;
    
    public GuiButton() {
        this.actionlisteners = new ArrayList<>();
        Arrays.setAll(colors, (i) -> new Color());
    }
    
    @Override
    protected void renderComponent(BorderedBatch2D batch, float aspect) {
        State state = enabled ? (hovering ? (clicked ? State.Clicked : State.Hovering) : State.Idle) : State.Disabled;
        Color c = colors[state.ordinal()];
        Texture t = textures[state.ordinal()];
        batch.color().set(c);
        batch.draw(t, getX(), getY(), getW(), getH());
    }
    
    @EventSubscription
    public void onMouseMoveEvent(InputEvent.MousePositionEvent ev) {
        this.hovering = ev.inViewport && posInBounds(ev.xRel, ev.yRel);
        if (!this.hovering && clicked) {
            clicked = false;
        }
    }
    
    @EventSubscription
    public void onMouseButtonEvent(InputEvent.MouseButtonEvent ev) {
        if (enabled) {
            if (hovering && ev.button == KeysAndButtons.OKE_MOUSE_BUTTON_LEFT
                    && ev.action == KeysAndButtons.OKE_PRESS) {
                clicked = true;
                actionlisteners.forEach((al) -> al.onAction(this));
                ev.consume();
            } else if (clicked && ev.button == KeysAndButtons.OKE_MOUSE_BUTTON_LEFT
                    && ev.action == KeysAndButtons.OKE_RELEASE) {
                clicked = false;
            }
        }
    }
    
    private boolean posInBounds(float x, float y) {
        return x >= getX() && y >= getY() && x <= getX() + getW() && y <= getY() + getH();
    }
    
    public void setEnabled(boolean b) {
        this.enabled = b;
    }
    
    public void setTexture(State state, Texture t) {
        this.textures[state.ordinal()] = t;
    }
    
    public Color color(State state) {
        return this.colors[state.ordinal()];
    }
    
    public void addActionListener(ActionListener al) {
        this.actionlisteners.add(al);
    }
    
}
