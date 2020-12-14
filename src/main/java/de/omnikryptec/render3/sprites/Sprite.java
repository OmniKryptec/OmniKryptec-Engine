package de.omnikryptec.render3.sprites;

import de.omnikryptec.render3.InstanceData;
import de.omnikryptec.render3.InstanceDataProvider;

public class Sprite implements InstanceDataProvider {
    
    private InstanceData instancedata;
    
    @Override
    public InstanceData getInstanceData() {
        return instancedata;
    }
    
}
