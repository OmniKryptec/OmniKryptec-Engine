package de.omnikryptec.util.math;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * A class to efficiently store 3D-transformations.
 * 
 * @author pcfreak9000
 *
 */
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
    
    /**
     * Sets the local transform by reading from the supplied transformation.
     * 
     * @param in the absolute local transform
     * @see #localspaceWrite()
     * @see #localspaceWrite(Consumer)
     */
    public void set(Matrix4fc in) {
        this.local.set(in);
        invalidate();
    }
    
    //this is maybe not so nice
    /**
     * Modifies the local transform and then invalidates this {@link Transform}.
     * 
     * @param action an action modifying the local transformation
     * @see #localspaceWrite()
     * @see #localspace()
     */
    public void localspaceWrite(Consumer<Matrix4f> action) {
        action.accept(local);
        invalidate();
    }
    
    /**
     * Provides the local transformation to be modified.
     * <p>
     * Note: invalidates this {@link Transform} BEFORE you can make any changes, so
     * listeners might use then-outdated values. If this is critical, use
     * {@link #localspaceWrite(Consumer)} instead.
     * </p>
     * 
     * @return the local transform
     * @see #localspace()
     */
    public Matrix4f localspaceWrite() {
        invalidate();
        return local;
    }
    
    /**
     * Read-only view of the local transform, in local space. If no parent is set,
     * this equals the {@link #worldspace()}.
     * 
     * @return transform in local space
     * @see #worldspace()
     */
    public Matrix4fc localspace() {
        return local;
    }
    
    /**
     * Read-only view of the transform, in world space.
     * 
     * @return transform in worldspace
     * @see #localspace()
     */
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
