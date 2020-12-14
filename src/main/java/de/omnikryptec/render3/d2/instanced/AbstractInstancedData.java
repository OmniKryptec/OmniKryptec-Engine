package de.omnikryptec.render3.d2.instanced;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.d2.InstanceData;

public abstract class AbstractInstancedData implements InstanceData {

    private Texture texture;
    
    public Texture getTexture() {
        return texture;
    }

    
}
