package de.omnikryptec.core.update;

import de.omnikryptec.util.data.DynamicArray;
import de.omnikryptec.util.updater.Time;

public class UContainer extends AbstractUpdateable {
    
    private DynamicArray<AbstractUpdateable> layers;
    private ILayer parentLayer;
    
    public UContainer() {
        super(false);
        layers = new DynamicArray<>();
    }
    
    public void setIUpdateable(int index, AbstractUpdateable layer) {
        AbstractUpdateable old = index < layers.size() ? layers.get(index) : null;
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
    public void update(Time time, UpdatePass pass) {
        for (AbstractUpdateable layer : layers) {
            layer.update(time, pass);
        }
    }
    
    @Override
    public void init(ILayer layer) {
        for (AbstractUpdateable ini : layers) {
            ini.init(layer);
        }
        parentLayer = layer;
    }
    
    @Override
    public void deinit(ILayer layer) {
        for (AbstractUpdateable deini : layers) {
            deini.deinit(layer);
        }
        parentLayer = null;
    }
}
