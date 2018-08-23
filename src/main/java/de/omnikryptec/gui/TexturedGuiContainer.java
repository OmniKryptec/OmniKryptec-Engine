package de.omnikryptec.gui;

import de.omnikryptec.graphics.SpriteBatch;
import de.omnikryptec.resource.texture.Texture;
import de.omnikryptec.util.Color;

public class TexturedGuiContainer extends GuiContainer{
	
	private Color color = new Color();
	private Texture texture;
	private float x,y,w,h;
	
	public TexturedGuiContainer(Texture t, float x, float y) {
		this.texture = t;
		this.x = x;
		this.y = y;
	}
		
	public TexturedGuiContainer setX(float x) {
		this.x = x;
		return this;
	}
	
	public TexturedGuiContainer setY(float y) {
		this.y = y;
		return this;
	}
	
	public TexturedGuiContainer setW(float w) {
		this.w = w;
		return this;
	}
	
	public TexturedGuiContainer setH(float h) {
		this.h = h;
		return this;
	}
	
	public TexturedGuiContainer setTexture(Texture t) {
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
	
	public Color getColor() {
		return color;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.color().setFrom(color);
		batch.draw(texture, x, y, w, h);
	}
	
}
