package de.omnikryptec.render.postprocessing;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.renderer.View;
import de.omnikryptec.util.updater.Time;

public class Bloom extends AbstractPostProcessor {

    private Postprocessor bloomThis;
    
    public Bloom() {
    }
    
    @Override
    public Texture postprocess(Time time, View view, Texture sceneRaw) {
        
        return null;
    }
    
}
