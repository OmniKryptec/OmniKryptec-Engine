package de.omnikryptec.render3;

public interface InstanceData extends InstanceDataProvider {
    
    Class<? extends BatchedRenderer> getDefaultRenderer();
    
    @Override
    default InstanceData getInstanceData() {
        return this;
    }
}
