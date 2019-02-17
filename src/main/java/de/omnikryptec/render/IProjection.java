package de.omnikryptec.render;

import org.joml.FrustumIntersection;
import org.joml.Matrix4fc;

public interface IProjection {

    Matrix4fc getRawProjection();

    Matrix4fc getProjection();

    FrustumIntersection getFrustumTester();

}
