package de.omnikryptec.render3;

import java.util.function.Supplier;

public interface InstanceData extends InstanceDataProvider {
    
    Class<? extends BatchedRenderer> getDefaultRenderer();
    
    @Override
    default InstanceData getInstanceData() {
        return this;
    }
}
