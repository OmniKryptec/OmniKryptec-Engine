package omnikryptec.gameobject;

import org.joml.Vector2f;

import omnikryptec.renderer.d2.RenderChunk2D;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.Maths;

public class Transform2D implements Positionable2D{
	
	protected Transform2D parent;
	protected Vector2f position;
	protected float rotation;
	protected Vector2f scale;
	
    public Transform2D() {
        this(new Vector2f(0));
    }

    public Transform2D(Vector2f pos) {
        this(pos, 0);
    }

    public Transform2D(Vector2f pos, float rot) {
        this(pos, rot, new Vector2f(1));
    }

    public Transform2D(Vector2f pos, float rot, Vector2f scale) {
        this(null, pos, rot, scale);
    }

    public Transform2D(Transform2D parent, Vector2f pos, float rot, Vector2f scale) {
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
        return new Transform2D(parent, getPositionNew(), getRotation(), getScaleNew());
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

    public Transform2D increaseRotation(float x) {
        this.rotation += x;
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

    public Transform2D setRotation(float x) {
        this.rotation = x;
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


    public float getRotation() {
        if (parent == null) {
            return rotation;
        }
        return parent.getRotation() + rotation;
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

    /**
     * the chunkx. used for rendering
     *
     * @see GameSettings#usesRenderChunking()
     * @return chunkx
     */
    public final long getChunkX() {
        return Maths.fastFloor(getPosition(true).x / RenderChunk2D.getWidth());
    }

    /**
     * the chunky. used for rendering
     *
     * @see GameSettings#usesRenderChunking()
     * @return chunky
     */
    public final long getChunkY() {
        return Maths.fastFloor(getPosition(true).y / RenderChunk2D.getHeight());
    }
    
    @Override
    public String toString() {
    	return "Position: "+position+" Rotation: "+rotation+" Scale: "+scale+((parent==null)?"":(" Parent: ["+parent.toString()+"] "));
    }
    
    public Transform2D addTransform(Transform2D t, boolean scale) {
    	this.position.add(t.getPosition(true));
    	this.rotation += t.getRotation();
    	if(scale) {
    		this.scale.add(t.getScale(true));
    	}
    	return this;
    }
}
