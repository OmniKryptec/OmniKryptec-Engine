package de.omnikryptec.gui.rendering;

import de.omnikryptec.gameobject.Camera;
import de.omnikryptec.graphics.SpriteBatch;
import de.omnikryptec.gui.GuiContainer;

public class GuiRenderer {

	private SpriteBatch batch;
	private GuiContainer parent;
	
	public GuiRenderer() {
		batch = new SpriteBatch(new Camera().setOrthographicProjection2D(0, 0, 1, 1));
	}
	
	public void paint() {
		if(parent!=null) {
			batch.begin();
			parent.update(batch,true);
			batch.end();
		}
	}
	
	public void setGui(GuiContainer gui) {
		this.parent = gui;
	}
}
