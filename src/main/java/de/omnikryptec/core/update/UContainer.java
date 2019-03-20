package de.omnikryptec.core.update;

import de.omnikryptec.util.data.DynamicArray;
import de.omnikryptec.util.updater.Time;

public class UContainer implements IUpdatable {
    
    private DynamicArray<IUpdatable> layers;
    private ILayer parentLayer;
    
    public UContainer() {
        layers = new DynamicArray<>();
    }
    
    public void setUpdatable(int index, IUpdatable layer) {
        IUpdatable old = index < layers.size() ? layers.get(index) : null;
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
    public boolean passive() {
        return false;
    }
    
    @Override
    public void update(Time time) {
        for (IUpdatable layer : layers) {
            layer.update(time);
        }
    }
    
    @Override
    public void init(ILayer layer) {
        for (IUpdatable updatable : layers) {
            updatable.init(layer);
        }
        parentLayer = layer;
    }
    
    @Override
    public void deinit(ILayer layer) {
        for (IUpdatable updatable : layers) {
            updatable.deinit(layer);
        }
        parentLayer = null;
    }
    
}
