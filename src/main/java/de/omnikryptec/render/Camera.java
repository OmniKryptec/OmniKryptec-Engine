/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.render;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.util.math.transform.Transform3Df;

public class Camera implements IProjection {

    private Matrix4f inverseCombined;

    private Matrix4f projectionMatrix;

    private Transform3Df transform;

    private final Matrix4f combined;
    private final FrustumIntersection frustumChecker;
    private boolean valid;

    public Camera(final Matrix4f projection) {
        this.projectionMatrix = projection;
        this.combined = new Matrix4f();
        this.inverseCombined = new Matrix4f();
        this.frustumChecker = new FrustumIntersection();
        this.valid = false;
        setTransform(new Transform3Df());
    }

    public void setProjection(final Matrix4f projection) {
        this.projectionMatrix = projection;
        this.valid = false;
    }

    @Override
    public Matrix4fc getRawProjection() {
        return this.projectionMatrix;
    }

    @Override
    public Matrix4fc getProjection() {
        revalidate();
        return this.combined;
    }

    @Override
    public FrustumIntersection getFrustumTester() {
        revalidate();
        return this.frustumChecker;
    }

    public Transform3Df getTransform() {
        return this.transform;
    }

    public void setTransform(final Transform3Df trans) {
        this.transform = trans;
        this.transform.setChangeNotifier((n) -> this.valid = false);
    }

    private void revalidate() {
        if (!this.valid) {
            //this.transform.worldspace().mul(this.projectionMatrix, this.combined);
            this.projectionMatrix.mul(this.transform.worldspace(), this.combined);
            this.frustumChecker.set(this.combined);
            this.inverseCombined = this.combined.invert(this.inverseCombined);
            this.valid = true;
        }
    }

    public Matrix4fc getProjectionInverse() {
        return this.inverseCombined;
    }
}
