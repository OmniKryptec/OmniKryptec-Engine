package de.omnikryptec.util.math.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class TransformBase<V, M, WV extends V, WM extends M, T extends TransformBase<V, M, WV, WM, T>> {
    
    private Consumer<T> transformChanged;
    
    private TransformBase<V, M, WV, WM, T> parent;
    private final List<TransformBase<V, M, WV, WM, T>> children = new ArrayList<>();
    
    private final WM transformMatrixWriteable;
    
    private final WV positionHelperVectorWriteable;
    
    private final WM localMatrixWriteable;
    private boolean changed;
    
    protected abstract WM createWM();
    
    protected abstract WV createWV();
    
    protected abstract void set(WM set, M in);
    
    protected abstract void mul(WM leftM, M rightM);
    
    protected abstract void getPosition(M from, WV target);
    
    protected abstract T thiz();
    
    protected TransformBase() {
        this.transformMatrixWriteable = createWM();
        this.localMatrixWriteable = createWM();
        this.positionHelperVectorWriteable = createWV();
    }
    
    public void setParent(final TransformBase<V, M, WV, WM, T> lParent) {
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
    public void set(final M in) {
        set(this.localMatrixWriteable, in);
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
    public void localspaceWrite(final Consumer<M> action) {
        action.accept(this.localMatrixWriteable);
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
    public WM localspaceWrite() {
        invalidate();
        return this.localMatrixWriteable;
    }
    
    /**
     * Read-only view of the local transform, in local space. If no parent is set,
     * this equals the {@link #worldspace()}.
     *
     * @return transform in local space
     * @see #worldspace()
     */
    public M localspace() {
        return this.localMatrixWriteable;
    }
    
    /**
     * Read-only view of the transform, in world space.
     *
     * @return transform in worldspace
     * @see #localspace()
     */
    public M worldspace() {
        revalidate();
        return this.transformMatrixWriteable;
    }
    
    public V worldspacePos() {
        revalidate();
        return this.positionHelperVectorWriteable;
    }
    
    public void setChangeNotifier(final Consumer<T> notified) {
        this.transformChanged = notified;
    }
    
    public Consumer<T> getChangeNotifier(final Consumer<T> notified) {
        return this.transformChanged;
    }
    
    private boolean changed() {
        return this.changed || (this.parent != null && this.parent.changed());
    }
    
    public TransformBase<V, M, WV, WM, T> getParent() {
        return this.parent;
    }
    
    public void revalidate() {
        if (changed()) {
            this.changed = false;
            set(this.transformMatrixWriteable, this.localMatrixWriteable);
            if (this.parent != null) {
                //TODx is this matrix multiplication correct? -> should be, the parent (the right matrix) transformation is and should be applied first
                mul(this.transformMatrixWriteable, this.parent.worldspace());
            }
            getPosition(this.transformMatrixWriteable, this.positionHelperVectorWriteable);
        }
    }
    
    private void invalidate() {
        this.changed = true;
        if (this.transformChanged != null) {
            this.transformChanged.accept(thiz());
        }
        for (final TransformBase<V, M, WV, WM, T> c : this.children) {
            c.invalidate();
        }
    }
    
}
