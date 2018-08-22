package omnikryptec.gui;

import omnikryptec.graphics.SpriteBatch;
import omnikryptec.resource.texture.Texture;

public class TexturedGuiObject extends GuiContainer{
	
	private Texture texture;
	private float x,y,w,h;
	
	public TexturedGuiObject(Texture t, float x, float y) {
		this.texture = t;
		this.x = x;
		this.y = y;
	}
		
	public TexturedGuiObject setX(float x) {
		this.x = x;
		return this;
	}
	
	public TexturedGuiObject setY(float y) {
		this.y = y;
		return this;
	}
	
	public TexturedGuiObject setW(float w) {
		this.w = w;
		return this;
	}
	
	public TexturedGuiObject setH(float h) {
		this.h = h;
		return this;
	}
	
	public TexturedGuiObject setTexture(Texture t) {
		this.texture = t;
		return this;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getW() {
		return w;
	}

	public float getH() {
		return h;
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(texture, x, y, w, h);
	}
	
}
