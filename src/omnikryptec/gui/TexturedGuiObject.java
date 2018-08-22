package omnikryptec.gui;

import omnikryptec.graphics.SpriteBatch;
import omnikryptec.resource.texture.Texture;

public class TexturedGuiObject extends GuiObject{
	
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
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(texture, x, y, w, h);
	}
	
}
