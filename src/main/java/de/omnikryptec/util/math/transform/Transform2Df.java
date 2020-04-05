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

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Transform2Df extends TransformBase<Vector2fc, Matrix3x2fc, Vector2f, Matrix3x2f, Transform2Df> {

    @Override
    protected Matrix3x2f createWM() {
        return new Matrix3x2f();
    }

    @Override
    protected Vector2f createWV() {
        return new Vector2f();
    }

    @Override
    protected void set(final Matrix3x2f set, final Matrix3x2fc in) {
        set.set(in);
    }

    @Override
    protected void mul(final Matrix3x2f leftM, final Matrix3x2fc rightM) {
        leftM.mul(rightM);
    }

    @Override
    protected void getPosition(final Matrix3x2fc from, final Vector2f target) {
        //untested; was different: old is making the vec 0 and then mulling it with the mat)
        target.set(from.m20(), from.m21());
    }

    @Override
    protected Transform2Df thiz() {
        return this;
    }

}
