package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;

public class PlayerComponent implements Component {
    public float maxXv, maxYv;

    public final float shOffsetX, shOffsetY;

    public PlayerComponent(final float maxXv, final float maxYv, final float ox, final float oy) {
        this.maxXv = maxXv;
        this.maxYv = maxYv;
        this.shOffsetX = ox;
        this.shOffsetY = oy;
    }

}
