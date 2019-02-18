package de.omnikryptec.render.batch;

import de.omnikryptec.util.data.Color;

public interface Batch {

    void begin();
    
    Color color();
    
    void flush();

    void end();
    
}
