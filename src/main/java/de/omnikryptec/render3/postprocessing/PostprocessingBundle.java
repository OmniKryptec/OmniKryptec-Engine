package de.omnikryptec.render3.postprocessing;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render3.structure.View;
import de.omnikryptec.util.updater.Time;

public class PostprocessingBundle implements Postprocessor {
    
    private List<Postprocessor> bundle = new ArrayList<>();
    
    public void add(Postprocessor p) {
        this.bundle.add(p);
    }
    
    @Override
    public Texture postprocess(Time time, View view, Texture sceneRaw) {
        Texture result = sceneRaw;
        for (int i = 0; i < bundle.size(); i++) {
            result = bundle.get(i).postprocess(time, view, result);
        }
        return result;
    }
}
