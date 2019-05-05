package de.omnikryptec.minigame;

import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import de.omnikryptec.ecs.component.Component;

public class PositionComponent implements Component {
    
    public final Matrix3x2f pos;
    
    public PositionComponent(float x, float y) {
        this.pos = new Matrix3x2f().setTranslation(x, y);
    }
    
}
