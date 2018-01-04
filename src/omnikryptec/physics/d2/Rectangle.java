package omnikryptec.physics.d2;

import org.joml.Rectanglef;

public class Rectangle extends Rectanglef {

	public Rectangle(float x, float y, float w, float h) {
		super(x, y, w + x, y + h);
	}

	public float getWidth() {
		return Math.abs(maxX-minX);
	}
	
	public float getHeight() {
		return Math.abs(maxY-minY);
	}
}
