package de.omnikryptec.util.math.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class TransformBase<V, M, WV extends V, WM extends M> {
    private final List<Consumer<TransformBase<V, M, WV, WM>>> transformChanged = new ArrayList<>();
    
    private TransformBase<V, M, WV, WM> parent;
    private final List<TransformBase<V, M, WV, WM>> children = new ArrayList<>();
    
    private final WM transform;
    
    private final WV positionHelper;
    
    private final WM local;
    private boolean changed;
    
    protected abstract WM createM();
    
    protected abstract WV createV();
    
    protected abstract void set(WM set, M in);
    
    protected abstract void mul(WM leftM, M rightM);
    
    protected abstract void getPosition(M from, WV target);
    
    public TransformBase() {
        this(null);
    }
    
    public TransformBase(final TransformBase<V, M, WV, WM> parent) {
        this.transform = createM();
        this.local = createM();
        this.positionHelper = createV();
        this.setParent(parent);
    }
    
    public void setParent(final TransformBase<V, M, WV, WM> lParent) {
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
        set(local, in);
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
        action.accept(this.local);
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
        return this.local;
    }
    
    /**
     * Read-only view of the local transform, in local space. If no parent is set,
     * this equals the {@link #worldspace()}.
     *
     * @return transform in local space
     * @see #worldspace()
     */
    public M localspace() {
        return this.local;
    }
    
    /**
     * Read-only view of the transform, in world space.
     *
     * @return transform in worldspace
     * @see #localspace()
     */
    public M worldspace() {
        revalidate();
        return this.transform;
    }
    
    public V worldspacePosition() {
        revalidate();
        return positionHelper;
    }
    
    public void addChangeNotifier(final Consumer<TransformBase<V, M, WV, WM>> notified) {
        this.transformChanged.add(notified);
    }
    
    public void removeChangeNotifier(final Consumer<TransformBase<V, M, WV, WM>> notified) {
        this.transformChanged.remove(notified);
    }
    
    private boolean changed() {
        return this.changed || (this.parent != null && this.parent.changed());
    }
    
    public TransformBase<V, M, WV, WM> getParent() {
        return parent;
    }
    
    private void revalidate() {
        if (changed()) {
            this.changed = false;
            set(transform, local);
            if (this.parent != null) {
                //TODx is this matrix multiplication correct? -> should be, the parent (the right matrix) transformation is and should be applied first 
                mul(transform, parent.worldspace());
            }
            getPosition(transform, positionHelper);
        }
    }
    
    private void invalidate() {
        this.changed = true;
        for (final Consumer<TransformBase<V, M, WV, WM>> c : this.transformChanged) {
            c.accept(this);
        }
        for (final TransformBase<V, M, WV, WM> c : this.children) {
            c.invalidate();
        }
    }
    
}
