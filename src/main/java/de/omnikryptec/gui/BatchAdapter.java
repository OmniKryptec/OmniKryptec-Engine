package de.omnikryptec.gui;

import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.render3.d2.instanced.InstancedBatch2D;
import de.omnikryptec.render3.d2.instanced.InstancedData;
import de.omnikryptec.util.data.Color;

public class BatchAdapter implements BorderedBatch2D {
    
    private InstancedData dataTmp = new InstancedData();
    private InstancedBatch2D actualBatch;
    
    public BatchAdapter(InstancedBatch2D actualBatch) {
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
    public void draw(Texture texture, Matrix3x2fc transform, float width, float height, boolean flipU, boolean flipV) {
        dataTmp.transform().identity();
        dataTmp.transform().set(transform);
        dataTmp.transform().scale(width, height);
        dataTmp.setUVAndTexture(texture, flipU, flipV);
        actualBatch.put(dataTmp);
    }
    
    @Override
    public void draw(Texture texture, float x, float y, float width, float height, boolean flipU, boolean flipV) {
        dataTmp.transform().identity();
        dataTmp.transform().setTranslation(x, y);
        dataTmp.transform().scale(width, height);
        dataTmp.setUVAndTexture(texture, flipU, flipV);
        actualBatch.put(dataTmp);
    }
    
    @Override
    public void drawPolygon(Texture texture, float[] poly, int start, int len) {
        throw new UnsupportedOperationException();
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
    public Vector2f borderOffset() {
        
        return dataTmp.offset();
    }
    
    @Override
    public Vector2f signedDistanceFieldData() {
        
        return dataTmp.sdData();
    }
    
}
