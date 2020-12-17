package de.omnikryptec.render3.d2.sprites;

import de.omnikryptec.render3.d2.instanced.InstancedBatch2D;
import de.omnikryptec.render3.d2.instanced.InstancedData;
import de.omnikryptec.util.math.transform.Transform2Df;

public class Sprite extends AbstractSprite {
    
    private InstancedData data = new InstancedData();
    private Transform2Df transform = new Transform2Df();
    
    public float tmpW=1, tmpH=1;//TODO fixme ugly and temporary
    
    @Override
    public IRenderer2D getRenderer() {
        return InstancedBatch2D.DEFAULT_BATCH;
    }
    
    @Override
    public void draw() {
        data.setTransform(transform.worldspace());
        data.getTransform().scale(tmpW, tmpH);
        InstancedBatch2D.DEFAULT_BATCH.put(data);
    }
    
    public InstancedData getRenderData() {
        return data;
    }
    
    public Transform2Df getTransform() {
        return transform;
    }
    
    public void setTransform(Transform2Df transform) {
        this.transform = transform;
    }
    public void setWidth(float w) {
        this.tmpW = w;
    }
    
    public void setHeight(float h) {
        this.tmpH = h;
    }
}
