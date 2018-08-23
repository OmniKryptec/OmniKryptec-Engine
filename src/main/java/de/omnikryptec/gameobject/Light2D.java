package de.omnikryptec.gameobject;

import omnikryptec.graphics.SpriteBatch;
import omnikryptec.resource.texture.Texture;

public class Light2D extends Sprite {

    public Light2D() {
        this("", null, null);
    }

    public Light2D(String name, Texture t) {
        this(name, t, null);
    }

    public Light2D(Texture t, GameObject2D p) {
        this("", t, p);
    }

    public Light2D(String name, GameObject2D p) {
        this(name, null, p);
    }

    public Light2D(String name, Texture texture, GameObject2D parent) {
        super(name, texture, parent);
    }

    @Override
    public void paint(SpriteBatch batch) {
        batch.draw(this, true);
    }
}
