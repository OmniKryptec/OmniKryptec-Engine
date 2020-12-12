package de.omnikryptec.render3;

import java.util.function.Supplier;

public interface InstanceData extends Supplier<InstanceData> {
    
    @Override
    default InstanceData get() {
        return this;
    }
}
