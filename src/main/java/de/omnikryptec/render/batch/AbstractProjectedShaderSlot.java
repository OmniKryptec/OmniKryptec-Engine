package de.omnikryptec.render.batch;

import de.omnikryptec.render.IProjection;

public abstract class AbstractProjectedShaderSlot extends AbstractShaderSlot {
    public abstract void setProjection(IProjection projection);
}
