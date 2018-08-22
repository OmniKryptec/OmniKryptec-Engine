package omnikryptec.gui;

import omnikryptec.graphics.SpriteBatch;
import omnikryptec.resource.texture.Texture;
import omnikryptec.util.Color;

public class ProgressBar extends TexturedGuiContainer {

	private Color barcolor = new Color();
	private Texture bartexture;
	private float value = 0;
	
	public ProgressBar(Texture t, Texture b, float x, float y) {
		super(t, x, y);
		this.bartexture = b;
	}

	public ProgressBar setValue(float b) {
		this.value = Math.min(1, Math.max(b, 0));
		return this;
	}

	public ProgressBar setBarTexture(Texture b) {
		this.bartexture = b;
		return this;
	}

	public float getValue() {
		return value;
	}

	public Color getBarColor() {
		return barcolor;
	}
	
	public ProgressBar setIndeterminate() {
		this.value = -1;
		return this;
	}
	
	public boolean isIndeterminate() {
		return this.value == -1;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.color().setFrom(getColor());
		if (getTexture() == null) {
			batch.fillRect(getX(), getY(), getW(), getH());
		} else {
			batch.draw(getTexture(), getX(), getY(), getW(), getH());
		}
		batch.color().setFrom(barcolor);
		if (bartexture == null) {
			batch.fillRect(getX(), getY(), value * getW(), getH());
		} else {
			batch.draw(bartexture, getX(), getY(), value * getW(), getH());
		}
	}

}
