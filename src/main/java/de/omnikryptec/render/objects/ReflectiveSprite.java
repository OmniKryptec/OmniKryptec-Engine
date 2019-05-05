package de.omnikryptec.render.objects;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;

public class ReflectiveSprite extends SimpleSprite {
    public static final RenderedObjectType TYPE = RenderedObjectType.of(ReflectiveSprite.class);
    
    private static final Matrix3x2f REFLECTION_MATRIX = new Matrix3x2f();
    
    static {
        REFLECTION_MATRIX._m11(-1);
    }
    
    public static enum Reflection2DType {
        Receive, Cast, Disable;
    }
    
    private Color reflectiveness = new Color();
    private Reflection2DType refl;
    private float offset=0;
    
    public void drawReflection(Batch2D batch) {
        if (refl == Reflection2DType.Cast) {
            batch.color().set(getColor() == null ? Color.ONE : getColor());
            Matrix3x2f mat = new Matrix3x2f(getTransform());
            mat.setTranslation(0, 0);
            mat.mulLocal(REFLECTION_MATRIX, mat);
            Vector2f v = getTransform().transformPosition(0, 0, new Vector2f());
            v.add(0, offset);
            mat.setTranslation(v);
            batch.draw(getTexture(), mat, getWidth(), getHeight(), false, false);
        }
    }
    
    public float getOffset() {
        return offset;
    }
    
    public void setOffset(float f) {
        this.offset = f;
    }
    
    public void setReflectionType(Reflection2DType en) {
        this.refl = en;
    }
    
    public Reflection2DType getReflectionType() {
        return refl;
    }
    
    public Color reflectiveness() {
        return reflectiveness;
    }
    
    @Override
    public RenderedObjectType type() {
        return TYPE;
    }
    
}
