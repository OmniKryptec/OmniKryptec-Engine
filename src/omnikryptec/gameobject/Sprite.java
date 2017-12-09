package omnikryptec.gameobject;

import omnikryptec.resource.texture.Texture;
import omnikryptec.util.Color;

public class Sprite extends GameObject2D{
	
	private Texture texture;
	private Color color = new Color(1,1,1,1);	
	private float layer = 0;
	
	public Sprite(String name, Texture texture, GameObject2D parent) {
		super(name, parent);
		this.texture = texture;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Color getColor() {
		return color;
	}
	
	public float getLayer() {
		return layer;
	}
	
	public Sprite setTexture(Texture t) {
		this.texture = t;
		return this;
	}
	
	public Sprite setColor(Color c) {
		this.color = c;
		return this;
	}
	
	public Sprite setLayer(float l) {
		this.layer = l;
		return this;
	}
	
}