package de.omnikryptec.graphics.render;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

public interface IProjection {

    Matrix4f getProjection();
    
    FrustumIntersection getFrustumTester();

}
