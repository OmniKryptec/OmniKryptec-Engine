package de.omnikryptec.render.batch;

import org.joml.Vector2fc;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.util.data.Color;

public abstract class AbstractAdvancedShaderSlot extends AbstractProjectedShaderSlot {

    public abstract void setReflection(Texture t);

    public abstract void setSignedDistanceData(Vector2fc vec);
    public abstract void setSDBorderData(Vector2fc vec);
    
    public abstract void setBorderColor(Color color);
    public abstract void setBorderOffset(Vector2fc vec);
    
}
