package de.omnikryptec.render.batch;

import de.omnikryptec.libapi.exposed.render.Texture;

public abstract class AbstractReflectedShaderSlot extends AbstractProjectedShaderSlot{
    
    public abstract void setReflection(Texture t);
    
}
