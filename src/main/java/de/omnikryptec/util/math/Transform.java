package de.omnikryptec.util.math;

import java.util.Iterator;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class Transform {
    
    private Transform parent;
    private Matrix4f transform;
    
    private Matrix4f local;
    private boolean changed;
    
    public Transform() {
        this(null);
    }
    
    public Transform(Transform parent) {
        this.parent = parent;
        this.transform = new Matrix4f();
        this.local = new Matrix4f();
        this.changed = true;
    }
    
    public Matrix4f modify() {
        changed = true;
        return local;
    }
    
    public Matrix4fc readonly() {
        return local;
    }
    
    private boolean changed() {
        return changed || (parent != null && parent.changed());
    }
    
    public Matrix4fc worldspace() {
        if (changed()) {
            changed = false;
            transform.identity();
            transform.set(local);
            if (parent != null) {
                //TODO does this work right?
                transform.mul(parent.worldspace());
            }
        }
        return transform;
    }
    
}
