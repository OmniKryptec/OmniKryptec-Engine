package de.omnikryptec.render.objects;

import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.data.Color;

public class AdvancedSprite extends SimpleSprite {
    public static final RenderedObjectType TYPE = RenderedObjectType.of(AdvancedSprite.class);
    
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
    
    @Override
    public RenderedObjectType type() {
        return TYPE;
    }
    
}
