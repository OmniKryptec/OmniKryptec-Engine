package de.omnikryptec.render.batch;

import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.math.transform.Transform3Df;

public abstract class AbstractProjectedShaderSlot extends AbstractShaderSlot {

    public abstract void setProjection(IProjection projection);

    public abstract void setTransform(Transform3Df transform);
}
