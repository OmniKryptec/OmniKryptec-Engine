package de.omnikryptec.minigame;

import org.joml.Vector2d;
import org.joml.Vector2f;

import de.omnikryptec.event.Event;

public class ShootEvent extends Event {
    
    public Vector2f dir;
    public float x, y;
    
    public ShootEvent(float x, float y, Vector2f dir) {
        this.dir = dir;
        this.x = x;
        this.y = y;
    }
    
}
