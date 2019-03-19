package de.omnikryptec.core.update;

import de.omnikryptec.util.data.DynamicArray;
import de.omnikryptec.util.updater.Time;

public class UContainer implements IUpdateable {
    
    private DynamicArray<IUpdateable> layers;
    private ILayer parentLayer;
    
    public UContainer() {
        layers = new DynamicArray<>();
    }
    
    public void setIUpdateable(int index, IUpdateable layer) {
        IUpdateable old = index < layers.size() ? layers.get(index) : null;
        layers.set(index, layer);
        if (parentLayer != null) {
            if (old != null) {
                old.deinit(parentLayer);
            }
            if (layer != null) {
                layer.init(parentLayer);
            }
        }
    }
    
    @Override
    public void update(Time time) {
        for (IUpdateable layer : layers) {
            layer.update(time);
        }
    }
    
    @Override
    public void init(ILayer layer) {
        for (IUpdateable ini : layers) {
            ini.init(layer);
        }
        parentLayer = layer;
    }
    
    @Override
    public void deinit(ILayer layer) {
        for (IUpdateable deini : layers) {
            deini.deinit(layer);
        }
        parentLayer = null;
    }

    @Override
    public boolean passive() {
        
        return false;
    }
}
