package de.omnikryptec.util;

import java.util.Objects;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import de.omnikryptec.util.data.Struct3f;

public class Struct2f {
    
    public final float x, y;
    
    public Struct2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public Struct2f(Vector2fc invec) {
        this(invec.x(), invec.y());
    }
    
    public Vector2f dynamic() {
        return new Vector2f(x, y);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Struct2f) {
            Struct2f other = (Struct2f) obj;
            return other.x == x && other.y == y;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
