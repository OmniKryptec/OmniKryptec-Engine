package de.omnikryptec.render.objects;

import org.joml.Matrix3x2f;

import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathf;

public class ReflectiveSprite extends SimpleSprite {
    public static final RenderedObjectType TYPE = RenderedObjectType.of(ReflectiveSprite.class);
    
    public static enum Reflection2DType {
        Receive, Cast, Disable;
    }
    
    private Color reflectiveness = new Color();
    private Reflection2DType refl;
    
    public void drawReflection(Batch2D batch) {
        if (refl == Reflection2DType.Cast) {
            batch.color().set(getColor() == null ? Color.ONE : getColor());
            Matrix3x2f mat = new Matrix3x2f();
            mat.translate(getPosition());
            mat.translate(getWidth(), 0);
            mat.rotate(Mathf.PI);
            
            batch.draw(getTexture(), mat, getWidth(), getHeight(), true, false);
        }
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
