package de.omnikryptec.util.data;

import java.util.Objects;

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
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Struct3f) {
            Struct3f other = (Struct3f) obj;
            return other.x == x && other.y == y && other.z == z;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
