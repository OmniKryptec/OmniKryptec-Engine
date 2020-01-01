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

import org.joml.Matrix3x2d;
import org.joml.Matrix3x2dc;
import org.joml.Vector2d;
import org.joml.Vector2dc;

public class Transform2Dd extends TransformBase<Vector2dc, Matrix3x2dc, Vector2d, Matrix3x2d, Transform2Dd> {
    
    @Override
    protected Matrix3x2d createWM() {
        return new Matrix3x2d();
    }
    
    @Override
    protected Vector2d createWV() {
        return new Vector2d();
    }
    
    @Override
    protected void set(final Matrix3x2d set, final Matrix3x2dc in) {
        set.set(in);
    }
    
    @Override
    protected void mul(final Matrix3x2d leftM, final Matrix3x2dc rightM) {
        leftM.mul(rightM);
    }
    
    @Override
    protected void getPosition(final Matrix3x2dc from, final Vector2d target) {
        target.set(from.m20(), from.m21());
    }
    
    @Override
    protected Transform2Dd thiz() {
        return this;
    }
    
}
