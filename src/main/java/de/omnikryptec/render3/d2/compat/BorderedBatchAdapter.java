package de.omnikryptec.render3.d2.compat;

import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.d2.BatchCache;
import de.omnikryptec.render3.d2.instanced.InstancedBatch2D;
import de.omnikryptec.render3.d2.instanced.InstancedData;
import de.omnikryptec.util.data.Color;

public class BorderedBatchAdapter implements BorderedBatch2D {
    
    private InstancedData dataTmp = new InstancedData();
    private InstancedBatch2D actualBatch;
    
    public BorderedBatchAdapter(InstancedBatch2D actualBatch) {
        this.actualBatch = actualBatch;
        setDefaultBDSFData();
        setDefaultBorderOffset();
        setDefaultSDFData();
    }
    
    public InstancedBatch2D getActualBatch() {
        return actualBatch;
    }
    
    @Override
    public void begin() {
        actualBatch.start();
    }
    
    @Override
    public Color color() {
        return dataTmp.color();
    }
    
    @Override
    public void end() {
        actualBatch.flush();
    }
    
    @Override
    public void draw(Texture texture, Matrix3x2fc transform, boolean flipU, boolean flipV) {
        dataTmp.getTransform().identity();
        dataTmp.getTransform().set(transform);
        dataTmp.setUVAndTexture(texture, flipU, flipV);
        actualBatch.put(dataTmp);
    }
    
    @Override
    public void draw(Texture texture, float x, float y, float width, float height, boolean flipU, boolean flipV) {
        dataTmp.getTransform().identity();
        dataTmp.getTransform().setTranslation(x, y);
        dataTmp.getTransform().scale(width, height);
        dataTmp.setUVAndTexture(texture, flipU, flipV);
        actualBatch.put(dataTmp);
    }
    
    @Override
    public Color borderColor() {
        return dataTmp.borderColor();
    }
    
    @Override
    public Vector2f borderSDFData() {
        return dataTmp.bsdData();
    }
    
    @Override
    public Vector2f signedDistanceFieldData() {
        return dataTmp.sdData();
    }
    
    @Override
    public void draw(BatchCache cache) {
        this.actualBatch.put(cache);
    }
    
    @Override
    public Vector2f borderOffset() {
        return this.dataTmp.offset();
    }
    
}
