package de.omnikryptec.util.math;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class Transform {
    
    private List<Consumer<Transform>> transformChanged = new ArrayList<>();
    
    private Transform parent;
    private List<Transform> children = new ArrayList<>();
    
    private Matrix4f transform;
    
    private Matrix4f local;
    private boolean changed;
    
    public Transform() {
        this(null);
    }
    
    public Transform(Transform parent) {
        this.transform = new Matrix4f();
        this.local = new Matrix4f();
        this.setParent(parent);
    }
    
    public void setParent(Transform lParent) {
        if (lParent != null) {
            lParent.children.add(this);
            this.parent = lParent;
        } else if (this.parent != null) {
            this.parent.children.remove(this);
            this.parent = null;
        }
        invalidate();
    }
    
    public void set(Matrix4fc in) {
        this.local.set(in);
        invalidate();
    }
    
    public Matrix4fc readonly() {
        return local;
    }
    
    public Matrix4fc worldspace() {
        revalidate();
        return transform;
    }
    
    public void addChangeNotifier(Consumer<Transform> notified) {
        this.transformChanged.add(notified);
    }
    
    public void removeChangeNotifier(Consumer<Transform> notified) {
        this.transformChanged.remove(notified);
    }
    
    private boolean changed() {
        return changed || (parent != null && parent.changed());
    }
    
    private void revalidate() {
        if (changed()) {
            changed = false;
            transform.identity();
            transform.set(local);
            if (parent != null) {
                //TODO is this correct?
                transform.mul(parent.worldspace());
            }
        }
    }
    
    private void invalidate() {
        changed = true;
        for (Consumer<Transform> c : transformChanged) {
            c.accept(this);
        }
        for (Transform c : children) {
            c.invalidate();
        }
    }
    
}
