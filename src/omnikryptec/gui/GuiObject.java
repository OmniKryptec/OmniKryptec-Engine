package omnikryptec.gui;

import omnikryptec.graphics.SpriteBatch;
import omnikryptec.resource.texture.Texture;

public class GuiObject {
	
	private Texture texture;
	private float x,y;
	
	public GuiObject(Texture t, float x, float y) {
		this.texture = t;
		this.x = x;
		this.y = y;
	}
	
	
	public GuiObject setX(float x) {
		this.x = x;
		return this;
	}
	
	public GuiObject setY(float y) {
		this.y = y;
		return this;
	}
	
	public void paint(SpriteBatch batch) {
		batch.draw(texture, x, y, 0.5f, 0.5f);
	}
	
}
