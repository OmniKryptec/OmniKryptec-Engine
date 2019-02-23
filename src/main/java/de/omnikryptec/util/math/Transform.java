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

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class to efficiently store 3D-transformations.
 *
 * @author pcfreak9000
 *
 */
public class Transform {

    private final List<Consumer<Transform>> transformChanged = new ArrayList<>();

    private Transform parent;
    private final List<Transform> children = new ArrayList<>();

    private final Matrix4f transform;

    private final Matrix4f local;
    private boolean changed;

    public Transform() {
        this(null);
    }

    public Transform(final Transform parent) {
        this.transform = new Matrix4f();
        this.local = new Matrix4f();
        this.setParent(parent);
    }

    public void setParent(final Transform lParent) {
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
    public void set(final Matrix4fc in) {
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
    public void localspaceWrite(final Consumer<Matrix4f> action) {
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
    public Matrix4f localspaceWrite() {
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
    public Matrix4fc localspace() {
        return this.local;
    }

    /**
     * Read-only view of the transform, in world space.
     *
     * @return transform in worldspace
     * @see #localspace()
     */
    public Matrix4fc worldspace() {
        revalidate();
        return this.transform;
    }

    public void addChangeNotifier(final Consumer<Transform> notified) {
        this.transformChanged.add(notified);
    }

    public void removeChangeNotifier(final Consumer<Transform> notified) {
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
                //TODO is this matrix multiplication correct?
                this.transform.mul(this.parent.worldspace());
            }
        }
    }

    private void invalidate() {
        this.changed = true;
        for (final Consumer<Transform> c : this.transformChanged) {
            c.accept(this);
        }
        for (final Transform c : this.children) {
            c.invalidate();
        }
    }

}
