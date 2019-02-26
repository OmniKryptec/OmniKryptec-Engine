package de.omnikryptec.minigame;

import org.joml.Vector2d;

import de.omnikryptec.event.Event;

public class ShootEvent extends Event {
    
    public Vector2d dir;
    public float x, y;
    
    public ShootEvent(float x, float y, Vector2d dir) {
        this.dir = dir;
        this.x = x;
        this.y = y;
    }
    
}
