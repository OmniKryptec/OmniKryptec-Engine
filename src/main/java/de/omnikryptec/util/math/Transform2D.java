/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.util.math;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * A class to efficiently store 2D-transformations.
 *
 * @author pcfreak9000
 *
 */
public class Transform2D {
    
    private final List<Consumer<Transform2D>> transformChanged = new ArrayList<>();
    
    private Transform2D parent;
    private final List<Transform2D> children = new ArrayList<>();
    
    private final Matrix3x2f transform;
    
    private final Vector2f positionHelper;
    
    private final Matrix3x2f local;
    private boolean changed;
    
    public Transform2D() {
        this(null);
    }
    
    public Transform2D(final Transform2D parent) {
        this.transform = new Matrix3x2f();
        this.local = new Matrix3x2f();
        this.positionHelper = new Vector2f();
        this.setParent(parent);
    }
    
    public void setParent(final Transform2D lParent) {
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
    public void set(final Matrix3x2fc in) {
        this.local.set(in);
        invalidate();
    }
    
    //this is maybe not so nice
    /**
     * Modifies the local transform and then invalidates this {@link Transform2D}.
     *
     * @param action an action modifying the local transformation
     * @see #localspaceWrite()
     * @see #localspace()
     */
    public void localspaceWrite(final Consumer<Matrix3x2fc> action) {
        action.accept(this.local);
        invalidate();
    }
    
    /**
     * Provides the local transformation to be modified.
     * <p>
     * Note: invalidates this {@link Transform2D} BEFORE you can make any changes,
     * so listeners might use then-outdated values. If this is critical, use
     * {@link #localspaceWrite(Consumer)} instead.
     * </p>
     *
     * @return the local transform
     * @see #localspace()
     */
    public Matrix3x2f localspaceWrite() {
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
    public Matrix3x2fc localspace() {
        return this.local;
    }
    
    /**
     * Read-only view of the transform, in world space.
     *
     * @return transform in worldspace
     * @see #localspace()
     */
    public Matrix3x2fc worldspace() {
        revalidate();
        return this.transform;
    }
    
    public Vector2fc worldspacePosition() {
        revalidate();
        return positionHelper;
    }
    
    public void addChangeNotifier(final Consumer<Transform2D> notified) {
        this.transformChanged.add(notified);
    }
    
    public void removeChangeNotifier(final Consumer<Transform2D> notified) {
        this.transformChanged.remove(notified);
    }
    
    private boolean changed() {
        return this.changed || (this.parent != null && this.parent.changed());
    }
    
    private void revalidate() {
        if (changed()) {
            this.changed = false;
            this.transform.identity();
            this.transform.set(this.local);
            if (this.parent != null) {
                //TODx is this matrix multiplication correct? -> should be, the parent (the right matrix) transformation is and should be applied first 
                this.transform.mul(this.parent.worldspace());
            }
            this.transform.transformPosition(positionHelper.set(0), positionHelper);
        }
    }
    
    private void invalidate() {
        this.changed = true;
        for (final Consumer<Transform2D> c : this.transformChanged) {
            c.accept(this);
        }
        for (final Transform2D c : this.children) {
            c.invalidate();
        }
    }
    
}
