package omnikryptec.physics.d2;

import org.joml.Rectanglef;

public class Rectangle extends Rectanglef {

	private float w, h;
	private boolean dynamic=false;
	
	
	public Rectangle() {
		this(0,0,0,0);
	}
	
	public Rectangle(float x, float y, float w, float h) {
		super(x, y, x + w, y + h);
		this.w = w;
		this.h = h;
	}

	public float getWidth() {
		return w;
	}

	public float getHeight() {
		return h;
	}

	public Rectangle setWidth(float w) {
		maxX = minX+w;
		return this;
	}
	
	public Rectangle setHeight(float h) {
		maxY = minY+h;
		return this;
	}
	
	public Rectangle setPosition(float x, float y) {
		minX = x;
		minY = y;
		maxX = x + w;
		maxY = y + h;
		return this;
	}
	
	public Rectangle setDynamic(boolean b) {
		this.dynamic = b;
		return this;
	}
	
	public boolean isDynamic() {
		return dynamic;
	}
	
	@Override
	public String toString() {
		return "x: "+minX+" y: "+minY+" w: "+w+"  h: "+h;
	}
	
}
