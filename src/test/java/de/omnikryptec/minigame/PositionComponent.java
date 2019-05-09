package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.util.math.transform.Transform2Df;

public class PositionComponent implements Component {
    
    public final Transform2Df transform;
    
    public PositionComponent(float x, float y) {
        this.transform = new Transform2Df();
        this.transform.localspaceWrite().setTranslation(x, y);
    }
    
}
