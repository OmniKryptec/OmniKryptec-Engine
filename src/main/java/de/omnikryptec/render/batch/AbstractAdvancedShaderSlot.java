package de.omnikryptec.render.batch;

import org.joml.Vector2fc;

import de.omnikryptec.libapi.exposed.render.Texture;

public abstract class AbstractAdvancedShaderSlot extends AbstractProjectedShaderSlot {

    public abstract void setReflection(Texture t);

    public abstract void setSignedDistanceData(Vector2fc vec);
    
}
