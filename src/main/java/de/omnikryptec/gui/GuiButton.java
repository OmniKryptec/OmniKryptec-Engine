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
    private State state;
    
    public GuiButton() {
        this.actionlisteners = new ArrayList<>();
        Arrays.setAll(colors, (i) -> new Color());
        this.state = State.Idle;
    }
    
    @Override
    protected void renderComponent(BorderedBatch2D batch, float aspect) {
        State state = enabled ? this.state : State.Disabled;
        Color c = colors[state.ordinal()];
        Texture t = textures[state.ordinal()];
        batch.color().set(c);
        batch.draw(t, getX(), getY(), getW(), getH());
    }
    
    @EventSubscription
    public void onMouseMoveEvent(InputEvent.MousePositionEvent ev) {
        if (state != State.Clicked && enabled) {
            checkHovering();
        }
    }
    
    private void checkHovering() {
        Vector2fc pos = Omnikryptec.getInput().getMousePositionRelative();//FIXME pcfreak9000 fix relative mouse pos ("random" exceptions)
        if (posInBounds(pos.x(), pos.y())) {
            state = State.Hovering;
        } else {
            state = State.Idle;
        }
    }
    
    @EventSubscription
    public void onMouseButtonEvent(InputEvent.MouseButtonEvent ev) {
        if (enabled) {
            if (state == State.Hovering && ev.button == KeysAndButtons.OKE_MOUSE_BUTTON_LEFT
                    && ev.action == KeysAndButtons.OKE_PRESS) {
                state = State.Clicked;
                actionlisteners.forEach((al) -> al.onAction(this));
                ev.consume();
            } else if (state == State.Clicked && ev.button == KeysAndButtons.OKE_MOUSE_BUTTON_LEFT
                    && ev.action == KeysAndButtons.OKE_RELEASE) {
                checkHovering();
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
