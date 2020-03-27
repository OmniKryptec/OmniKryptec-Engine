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

package de.omnikryptec.render.objects;

import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.data.Color;

public class AdvancedSprite extends SimpleSprite {
    
    private static final Matrix3x2f REFLECTION_MATRIX = new Matrix3x2f();
    
    static {
        REFLECTION_MATRIX._m11(-1);
    }
    
    public static enum Reflection2DType {
        Receive, Cast, Disable;
    }
    
    private final Color reflectiveness = new Color();
    private Reflection2DType refl;
    private float offset = 0;
    
    private float tiling = 1;
    
    public void drawReflection(final Batch2D batch) {
        if (this.refl == Reflection2DType.Cast) {
            batch.color().set(getColor() == null ? Color.ONE : getColor());
            final Matrix3x2f mat = new Matrix3x2f(getTransform().worldspace());
            mat.setTranslation(0, 0);
            mat.mulLocal(REFLECTION_MATRIX, mat);
            final Vector2f v = getTransform().worldspace().transformPosition(0, 0, new Vector2f());
            v.add(0, this.offset);
            mat.setTranslation(v);
            batch.draw(getTexture(), mat, getWidth(), getHeight(), false, false);
        }
    }
    
    public float getOffset() {
        return this.offset;
    }
    
    public void setOffset(final float f) {
        this.offset = f;
    }
    
    public void setReflectionType(final Reflection2DType en) {
        this.refl = en;
    }
    
    public Reflection2DType getReflectionType() {
        return this.refl;
    }
    
    public Color reflectiveness() {
        return this.reflectiveness;
    }
    
    public float getTilingFactor() {
        return tiling;
    }
    
    public void setTilingFactor(float f) {
        this.tiling = f;
    }
    
}
