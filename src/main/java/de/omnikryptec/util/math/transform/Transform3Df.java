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

package de.omnikryptec.util.math.transform;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Transform3Df extends TransformBase<Vector3fc, Matrix4fc, Vector3f, Matrix4f, Transform3Df> {

    @Override
    protected Matrix4f createWM() {
        return new Matrix4f();
    }

    @Override
    protected Vector3f createWV() {
        return new Vector3f();
    }

    @Override
    protected void set(final Matrix4f set, final Matrix4fc in) {
        set.set(in);
    }

    @Override
    protected void mul(final Matrix4f leftM, final Matrix4fc rightM) {
        leftM.mul(rightM);
    }

    @Override
    protected void getPosition(final Matrix4fc from, final Vector3f target) {
        //untested; this was like it is in Transform2Df
        target.set(from.m30(), from.m31(), from.m32());
    }

    @Override
    protected Transform3Df thiz() {
        return this;
    }

}
