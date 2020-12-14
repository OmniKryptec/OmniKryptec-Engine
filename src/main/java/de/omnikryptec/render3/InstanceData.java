package de.omnikryptec.render3;

public interface InstanceData extends InstanceDataProvider {
    
    Class<? extends IBatchedRenderer2D> getDefaultRenderer();
    
    @Override
    default InstanceData getInstanceData() {
        return this;
    }
}
