package de.omnikryptec.render3.instancedrect;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.InstanceData;

public abstract class InstancedRectData implements InstanceData {

    private Texture texture;
    
    public Texture getTexture() {
        return texture;
    }

    
}
