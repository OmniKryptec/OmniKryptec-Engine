package de.omnikryptec.util;

import java.util.Objects;

import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Struct2f {

    public final float x, y;

    public Struct2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public Struct2f(final Vector2fc invec) {
        this(invec.x(), invec.y());
    }

    public Vector2f dynamic() {
        return new Vector2f(this.x, this.y);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Struct2f) {
            final Struct2f other = (Struct2f) obj;
            return other.x == this.x && other.y == this.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
}
