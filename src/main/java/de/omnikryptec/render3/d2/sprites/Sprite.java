package de.omnikryptec.render3.d2.sprites;

import de.omnikryptec.render3.d2.instanced.InstancedBatch2D;
import de.omnikryptec.render3.d2.instanced.InstancedData;

public class Sprite extends AbstractSprite {
    
    private InstancedData data = new InstancedData();
    
    @Override
    public IRenderer2D getRenderer() {
        return InstancedBatch2D.DEFAULT_BATCH;
    }
    
    @Override
    public void draw() {
        InstancedBatch2D.DEFAULT_BATCH.put(data);
    }
    
    public InstancedData getRenderData() {
        return data;
    }
    
}
