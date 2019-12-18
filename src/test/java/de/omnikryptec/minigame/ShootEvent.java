package de.omnikryptec.minigame;

import org.joml.Vector2f;

import de.omnikryptec.event.Event;

public class ShootEvent extends Event {
    
    public static enum Projectile {
        Normal, Bomb;
    }
    
    public Vector2f dir;
    public float x, y;
    public float range;
    public Projectile projectile;
    
    private ShootEvent(final float x, final float y, final Vector2f dir) {
        this.dir = dir;
        this.x = x;
        this.y = y;
    }
    
    public ShootEvent(final float x, final float y, final Vector2f dir, final float range, final Projectile pro) {
        this(x, y, dir);
        this.range = range;
        this.projectile = pro;
    }
    
}
