package de.omnikryptec.util.data;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Struct3f {
    
    public final float x, y, z;
    
    public Struct3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Struct3f(Vector3fc invec) {
        this(invec.x(), invec.y(), invec.z());
    }
    
    public Vector3f dynamic() {
        return new Vector3f(x, y, z);
    }
}
