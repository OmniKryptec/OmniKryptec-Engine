package de.omnikryptec.util;

import org.joml.Vector2f;
import org.joml.Vector2fc;

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
    
}
