package omnikryptec.gameobject;

import org.joml.Vector2f;

public class Transform2D implements Positionable2D{
	
	protected Transform2D parent;
	protected Vector2f position;
	protected Vector2f rotation;
	protected Vector2f scale;
	
    public Transform2D() {
        this(new Vector2f(0));
    }

    public Transform2D(Vector2f pos) {
        this(pos, new Vector2f(0));
    }

    public Transform2D(Vector2f pos, Vector2f rot) {
        this(pos, rot, new Vector2f(1));
    }

    public Transform2D(Vector2f pos, Vector2f rot, Vector2f scale) {
        this(null, pos, rot, scale);
    }

    public Transform2D(Transform2D parent, Vector2f pos, Vector2f rot, Vector2f scale) {
        this.parent = parent;
        this.position = pos;
        this.rotation = rot;
        this.scale = scale;
    }

    public Transform2D(Transform2D parent) {
        this();
        setParent(parent);
    }

    public Transform2D getNewCopy() {
        return new Transform2D(parent, getPositionNew(), getRotationNew(), getScaleNew());
    }

    public Transform2D setParent(Transform2D transform) {
        this.parent = transform;
        return this;
    }

    public Transform2D getParent() {
        return parent;
    }

    public Transform2D setX(float x) {
        this.position.x = x;
        return this;
    }

    public Transform2D setY(float y) {
        this.position.y = y;
        return this;
    }


    public Transform2D increasePosition(float x, float y) {
        this.position.x += x;
        this.position.y += y;
        return this;
    }

    public Transform2D increaseRotation(float x, float y) {
        this.rotation.x += x;
        this.rotation.y += y;
        return this;
    }
    
    public Transform2D increaseScale(float x, float y) {
        this.scale.x += x;
        this.scale.y += y;
        return this;
    }

    public Transform2D setPosition(float x, float y) {
        this.position.set(x, y);
        return this;
    }

    public Transform2D setRotation(float x, float y) {
        this.rotation.set(x, y);
        return this;
    }

    public Transform2D setScale(float x, float y) {
        this.scale.set(x, y);
        return this;
    }

    public Transform2D setScale(float d) {
        return setScale(d, d);
    }

    public Transform2D setPosition(Vector2f pos) {
        this.position = pos;
        return this;
    }

    public Transform2D setRotation(Vector2f q) {
        this.rotation = q;
        return this;
    }

    public Transform2D setScale(Vector2f scale) {
        this.scale = scale;
        return this;
    }

    public Vector2f getPositionNew() {
        return new Vector2f(position);
    }

    public Vector2f getRotationNew() {
        return new Vector2f(rotation);
    }

    public Vector2f getScaleNew() {
        return new Vector2f(scale);
    }

    public Vector2f getPositionSimple() {
        return position;
    }

    public Vector2f getRotationSimple() {
        return rotation;
    }

    public Vector2f getScaleSimple() {
        return scale;
    }

    @Override
    public Vector2f getPosition() {
        return getPosition(false);
    }

    public Vector2f getPosition(boolean simple) {
        if (parent == null) {
            return simple ? position : new Vector2f(position);
        }
        return parent.getPosition(false).add(position);
    }

    public Vector2f getRotation() {
        return getRotation(false);
    }

    public Vector2f getRotation(boolean simple) {
        if (parent == null) {
            return simple ? rotation : new Vector2f(rotation);
        }
        return parent.getRotation(false).add(rotation);
    }

    public Vector2f getScale() {
        return getScale(false);
    }

    public Vector2f getScale(boolean simple) {
        if (parent == null) {
            return simple ? scale : new Vector2f(scale);
        }
        return parent.getScale(false).add(scale);
    }
}
