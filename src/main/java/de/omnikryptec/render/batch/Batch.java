package de.omnikryptec.render.batch;

import org.joml.Matrix4fc;

import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.data.Color;

public interface Batch {

    void begin();

    void setGlobalTransform(Matrix4fc mat);

    void setProjection(IProjection projection);

    Color color();

    void flush();

    void end();
}
