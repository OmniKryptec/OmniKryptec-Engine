package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.objects.RenderedObject;
import de.omnikryptec.util.data.Color;

public class RenderComponent implements Component {
    
    public float w, h;
    public Color color;
    public int layer;
    
    public RenderedObject backingSprite;
    
    public Texture texture;
    
    private RenderComponent(final float w, final float h) {
        this.w = w;
        this.h = h;
    }
    
    public RenderComponent(final float w, final float h, final Color color, final int layer) {
        this(w, h);
        this.color = color;
        this.layer = layer;
    }
}
