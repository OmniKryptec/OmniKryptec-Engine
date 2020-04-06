package de.omnikryptec.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Vector2fc;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.input.InputEvent;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.BorderedBatch2D;
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
        this.hovering = posInBounds(ev.xRel, ev.yRel);
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
